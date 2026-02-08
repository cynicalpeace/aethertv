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

            for (country in countries) {
                val url = "https://iptv-org.github.io/epg/guides/${country.lowercase()}.xml"
                try {
                    withContext(Dispatchers.IO) {
                        val response = httpClient.get(url)
                        val inputStream = response.bodyAsChannel().toInputStream()
                        val channels = mutableListOf<com.aethertv.data.local.entity.EpgChannelEntity>()
                        val programs = mutableListOf<com.aethertv.data.local.entity.EpgProgramEntity>()
                        xmltvParser.parse(
                            inputStream = inputStream,
                            onChannel = { channels.add(it) },
                            onProgram = { programs.add(it) },
                        )
                        epgRepository.insertChannels(channels)
                        epgRepository.insertPrograms(programs)
                    }
                } catch (_: Exception) {
                    // Continue with other countries
                }
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "epg_refresh_periodic"
    }
}
