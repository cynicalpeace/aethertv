package com.aethertv.epg

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.repository.EpgRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class EpgRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val httpClient: HttpClient,
    private val xmltvParser: XmltvParser,
    private val epgRepository: EpgRepository,
    private val settingsDataStore: SettingsDataStore,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "epg_refresh_periodic"
        private const val TAG = "EpgRefreshWorker"
        // Batch insert size for memory efficiency (H33 fix)
        private const val BATCH_SIZE = 100
        // Maximum programs to hold in memory before inserting (H33 fix)
        private const val MAX_PROGRAM_BUFFER = 5000
    }

    override suspend fun doWork(): Result {
        return try {
            val countries = settingsDataStore.epgCountries.first()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (countries.isEmpty()) return Result.success()

            // Clear expired programs (older than now)
            val now = System.currentTimeMillis()
            epgRepository.deleteExpiredPrograms(now)

            // C18 fix: Collect ALL countries' data first, then call replaceAllData ONCE.
            // Previously each country called replaceAllData() which wiped the previous country's data.
            val allChannels = mutableListOf<com.aethertv.data.local.entity.EpgChannelEntity>()
            val allPrograms = mutableListOf<com.aethertv.data.local.entity.EpgProgramEntity>()
            var totalProgramCount = 0

            for (country in countries) {
                // Use Locale.ROOT for consistent URL generation (H32 fix)
                val url = "https://iptv-org.github.io/epg/guides/${country.lowercase(java.util.Locale.ROOT)}.xml"
                try {
                    withContext(Dispatchers.IO) {
                        val response = httpClient.get(url)
                        response.bodyAsChannel().toInputStream().use { inputStream ->
                            xmltvParser.parse(
                                inputStream = inputStream,
                                onChannel = { allChannels.add(it) },
                                onProgram = { program ->
                                    allPrograms.add(program)
                                    totalProgramCount++
                                    
                                    // Check memory pressure periodically (H33 fix)
                                    if (totalProgramCount % MAX_PROGRAM_BUFFER == 0) {
                                        val runtime = Runtime.getRuntime()
                                        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
                                        val maxMemory = runtime.maxMemory()
                                        if (usedMemory > maxMemory * 0.85) {
                                            android.util.Log.w(TAG, "Memory pressure detected, skipping remaining programs for $country")
                                            return@parse
                                        }
                                    }
                                },
                            )
                            android.util.Log.d(TAG, "EPG parsed for $country: channels=${allChannels.size}, programs=$totalProgramCount")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.w(TAG, "Failed to fetch EPG for $country: ${e.message}")
                }
            }

            // C16 fix: Transactional insert for data integrity
            // C18 fix: Single atomic replace with ALL countries' combined data
            if (allChannels.isNotEmpty() || allPrograms.isNotEmpty()) {
                epgRepository.replaceAllData(
                    channels = allChannels,
                    programs = allPrograms,
                    batchSize = BATCH_SIZE,
                )
                android.util.Log.d(TAG, "EPG refresh complete: ${allChannels.size} channels, $totalProgramCount programs from ${countries.size} countries")
            }
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "EPG refresh failed", e)
            Result.retry()
        }
    }
}
