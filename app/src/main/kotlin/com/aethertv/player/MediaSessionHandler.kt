package com.aethertv.player

import android.content.Context
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages MediaSession lifecycle.
 * 
 * MediaSession is created on initialize() and released on shutdown().
 * Application should call initialize() in onCreate() and shutdown() in onTerminate().
 */
@Singleton
class MediaSessionHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
) {
    
    companion object {
        private const val TAG = "MediaSessionHandler"
    }
    
    private var mediaSession: MediaSession? = null
    private var isInitialized = false

    /**
     * Initialize the media session.
     * Should be called once from Application.onCreate().
     */
    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        
        createSession()
    }
    
    private fun createSession() {
        if (mediaSession == null) {
            try {
                mediaSession = MediaSession.Builder(context, exoPlayer).build()
                Log.d(TAG, "MediaSession created")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create MediaSession", e)
            }
        }
    }

    /**
     * Release the media session.
     */
    fun release() {
        try {
            mediaSession?.release()
            mediaSession = null
            Log.d(TAG, "MediaSession released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaSession", e)
        }
    }
    
    /**
     * Clean shutdown.
     * Call from Application.onTerminate().
     */
    fun shutdown() {
        release()
        isInitialized = false
    }
}
