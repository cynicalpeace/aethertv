package com.aethertv.verification

import android.content.Context
import android.util.Log
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

/**
 * Detects stream quality by probing the video resolution.
 * Properly manages ExoPlayer lifecycle to prevent leaks.
 */
@Singleton
class QualityDetector @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val TAG = "QualityDetector"
    }

    @OptIn(UnstableApi::class)
    suspend fun detect(
        playbackUrl: String,
        timeout: Duration = 15.seconds,
    ): StreamQuality {
        return withTimeoutOrNull(timeout) {
            suspendCancellableCoroutine { cont ->
                var player: ExoPlayer? = null
                var hasResumed = false
                
                try {
                    player = ExoPlayer.Builder(context).build()
                    
                    player.addListener(object : Player.Listener {
                        override fun onVideoSizeChanged(videoSize: VideoSize) {
                            if (hasResumed) return // Prevent double resume
                            hasResumed = true
                            
                            val quality = when {
                                videoSize.height >= 1080 -> StreamQuality.FHD_1080P
                                videoSize.height >= 720 -> StreamQuality.HD_720P
                                videoSize.height >= 480 -> StreamQuality.SD_480P
                                else -> StreamQuality.LOW
                            }
                            
                            // Release player before resuming
                            try {
                                player?.release()
                            } catch (e: Exception) {
                                Log.w(TAG, "Error releasing player: ${e.message}")
                            }
                            
                            cont.resume(quality)
                        }
                        
                        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                            if (hasResumed) return
                            hasResumed = true
                            
                            Log.w(TAG, "Player error during quality detection: ${error.message}")
                            
                            try {
                                player?.release()
                            } catch (e: Exception) {
                                Log.w(TAG, "Error releasing player after error: ${e.message}")
                            }
                            
                            cont.resume(StreamQuality.UNKNOWN)
                        }
                    })
                    
                    player.setMediaItem(MediaItem.fromUri(playbackUrl))
                    player.prepare()
                    
                    cont.invokeOnCancellation {
                        if (!hasResumed) {
                            try {
                                player?.release()
                            } catch (e: Exception) {
                                Log.w(TAG, "Error releasing player on cancellation: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up quality detection", e)
                    // Ensure player is released on setup error
                    try {
                        player?.release()
                    } catch (releaseError: Exception) {
                        Log.w(TAG, "Error releasing player after setup failure: ${releaseError.message}")
                    }
                    if (!hasResumed) {
                        hasResumed = true
                        cont.resume(StreamQuality.UNKNOWN)
                    }
                }
            }
        } ?: StreamQuality.UNKNOWN
    }
}
