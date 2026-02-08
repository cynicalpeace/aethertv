package com.aethertv.player

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSessionHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
) {
    private var mediaSession: MediaSession? = null

    fun initialize() {
        if (mediaSession == null) {
            mediaSession = MediaSession.Builder(context, exoPlayer).build()
        }
    }

    fun release() {
        mediaSession?.release()
        mediaSession = null
    }
}
