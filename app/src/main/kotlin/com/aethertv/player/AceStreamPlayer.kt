package com.aethertv.player

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.data.remote.StreamInfo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages AceStream playback with proper synchronization.
 * Uses mutex to prevent race conditions between play/stop operations.
 */
@Singleton
class AceStreamPlayer @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val engineClient: AceStreamEngineClient,
) {
    companion object {
        private const val TAG = "AceStreamPlayer"
    }
    
    private val mutex = Mutex()
    private var currentStreamInfo: StreamInfo? = null

    /**
     * Start playing a stream by infohash.
     * Thread-safe: uses mutex to prevent race conditions with stop().
     */
    suspend fun play(infohash: String) {
        mutex.withLock {
            // Stop current stream if any
            stopInternal()
            
            try {
                val streamInfo = engineClient.requestStream(infohash)
                currentStreamInfo = streamInfo
                val mediaItem = MediaItem.fromUri(streamInfo.playbackUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start stream: $infohash", e)
                currentStreamInfo = null
                throw e
            }
        }
    }

    /**
     * Stop the current stream.
     * Thread-safe: uses mutex to prevent race conditions with play().
     */
    suspend fun stop() {
        mutex.withLock {
            stopInternal()
        }
    }
    
    /**
     * Internal stop logic - caller must hold mutex.
     */
    private suspend fun stopInternal() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        currentStreamInfo?.let { info ->
            try {
                engineClient.stopStream(info.commandUrl)
            } catch (e: Exception) {
                Log.d(TAG, "Failed to stop stream (may already be stopped): ${e.message}")
            }
        }
        currentStreamInfo = null
    }

    fun getPlayer(): ExoPlayer = exoPlayer

    fun isPlaying(): Boolean = exoPlayer.isPlaying
}
