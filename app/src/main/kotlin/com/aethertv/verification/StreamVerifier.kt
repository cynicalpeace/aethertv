package com.aethertv.verification

import android.util.Log
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.domain.model.StreamQuality
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

sealed class VerificationResult {
    data class Alive(
        val infohash: String,
        val peers: Int,
        val quality: StreamQuality,
        val bitrate: Long,
    ) : VerificationResult()

    data class Dead(val infohash: String) : VerificationResult()
    data class Error(val infohash: String, val message: String?) : VerificationResult()
}

/**
 * Verifies stream availability and quality.
 * Uses QualityDetector to avoid code duplication.
 */
@Singleton
class StreamVerifier @Inject constructor(
    private val engineClient: AceStreamEngineClient,
    private val qualityDetector: QualityDetector,
) {
    companion object {
        private const val TAG = "StreamVerifier"
    }
    
    private val semaphore = Semaphore(1) // Max 1 concurrent verification
    private val delayBetweenChecks = 10.seconds

    suspend fun verify(infohash: String): VerificationResult {
        return semaphore.withPermit {
            var commandUrl: String? = null
            try {
                val streamInfo = engineClient.requestStream(infohash)
                commandUrl = streamInfo.commandUrl
                val stats = engineClient.getStats(streamInfo.statUrl)

                if (stats.peers == 0) {
                    engineClient.stopStream(commandUrl)
                    return@withPermit VerificationResult.Dead(infohash)
                }

                // Use the shared QualityDetector to avoid code duplication
                val quality = qualityDetector.detect(streamInfo.playbackUrl)
                engineClient.stopStream(commandUrl)

                VerificationResult.Alive(
                    infohash = infohash,
                    peers = stats.peers,
                    quality = quality,
                    bitrate = stats.speedDown,
                )
            } catch (e: Exception) {
                Log.w(TAG, "Verification failed for $infohash: ${e.message}")
                // Ensure we stop the stream even on error
                commandUrl?.let {
                    try {
                        engineClient.stopStream(it)
                    } catch (stopError: Exception) {
                        Log.d(TAG, "Failed to stop stream after error: ${stopError.message}")
                    }
                }
                VerificationResult.Error(infohash, e.message)
            } finally {
                delay(delayBetweenChecks)
            }
        }
    }
}
