package com.aethertv.verification

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.domain.model.StreamQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
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

@Singleton
class StreamVerifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val engineClient: AceStreamEngineClient,
) {
    private val semaphore = Semaphore(1) // Max 1 concurrent verification
    private val delayBetweenChecks = 10.seconds

    suspend fun verify(infohash: String): VerificationResult {
        return semaphore.withPermit {
            try {
                val streamInfo = engineClient.requestStream(infohash)
                val stats = engineClient.getStats(streamInfo.statUrl)

                if (stats.peers == 0) {
                    engineClient.stopStream(streamInfo.commandUrl)
                    return@withPermit VerificationResult.Dead(infohash)
                }

                val quality = detectQuality(streamInfo.playbackUrl)
                engineClient.stopStream(streamInfo.commandUrl)

                VerificationResult.Alive(
                    infohash = infohash,
                    peers = stats.peers,
                    quality = quality,
                    bitrate = stats.speedDown,
                )
            } catch (e: Exception) {
                VerificationResult.Error(infohash, e.message)
            } finally {
                delay(delayBetweenChecks)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private suspend fun detectQuality(playbackUrl: String): StreamQuality {
        return withTimeoutOrNull(15.seconds) {
            suspendCancellableCoroutine { cont ->
                val player = ExoPlayer.Builder(context).build()
                player.addListener(object : Player.Listener {
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        val quality = when {
                            videoSize.height >= 1080 -> StreamQuality.FHD_1080P
                            videoSize.height >= 720 -> StreamQuality.HD_720P
                            videoSize.height >= 480 -> StreamQuality.SD_480P
                            else -> StreamQuality.LOW
                        }
                        player.release()
                        cont.resume(quality)
                    }
                })
                player.setMediaItem(MediaItem.fromUri(playbackUrl))
                player.prepare()
                cont.invokeOnCancellation { player.release() }
            }
        } ?: StreamQuality.UNKNOWN
    }
}
