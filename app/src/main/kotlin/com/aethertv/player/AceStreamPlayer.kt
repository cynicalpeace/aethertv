package com.aethertv.player

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.data.remote.StreamInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AceStreamPlayer @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val engineClient: AceStreamEngineClient,
) {
    private var currentStreamInfo: StreamInfo? = null

    suspend fun play(infohash: String) {
        stop()
        val streamInfo = engineClient.requestStream(infohash)
        currentStreamInfo = streamInfo
        val mediaItem = MediaItem.fromUri(streamInfo.playbackUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    suspend fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        currentStreamInfo?.let { info ->
            try {
                engineClient.stopStream(info.commandUrl)
            } catch (_: Exception) {
                // Engine may already have cleaned up
            }
        }
        currentStreamInfo = null
    }

    fun getPlayer(): ExoPlayer = exoPlayer

    fun isPlaying(): Boolean = exoPlayer.isPlaying
}
