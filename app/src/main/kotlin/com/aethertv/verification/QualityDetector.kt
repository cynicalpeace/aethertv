package com.aethertv.verification

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.domain.model.StreamQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class QualityDetector @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @OptIn(UnstableApi::class)
    suspend fun detect(
        playbackUrl: String,
        timeout: Duration = 15.seconds,
    ): StreamQuality {
        return withTimeoutOrNull(timeout) {
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
