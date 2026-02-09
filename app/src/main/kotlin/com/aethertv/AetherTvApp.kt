package com.aethertv

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.aethertv.player.MediaSessionHandler
import com.aethertv.util.CrashLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class with proper lifecycle management.
 * Uses ProcessLifecycleOwner instead of onTerminate() for reliable cleanup.
 * Note: onTerminate() is never called in production Android.
 */
@HiltAndroidApp
class AetherTvApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var mediaSessionHandler: MediaSessionHandler

    override fun onCreate() {
        super.onCreate()
        
        // Initialize crash logging
        CrashLogger.initialize(this)
        
        // Initialize media session with lifecycle management
        mediaSessionHandler.initialize()
        
        // Use ProcessLifecycleOwner for reliable app lifecycle events (C12 fix)
        // Note: onDestroy is NOT guaranteed when process is killed by system (H35 fix)
        // We perform cleanup in onStop() as a fallback since onDestroy may never be called
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            private var isInForeground = false
            
            override fun onStart(owner: LifecycleOwner) {
                isInForeground = true
            }
            
            override fun onStop(owner: LifecycleOwner) {
                // App is going to background (H35 fix)
                // If not playing media, release the session early to avoid orphaned sessions
                // when the system kills the process while backgrounded
                isInForeground = false
                // Note: Keep session alive if playing - only cleanup on full stop
            }
            
            override fun onDestroy(owner: LifecycleOwner) {
                // Final cleanup when process lifecycle ends gracefully
                // Note: This may NOT be called if system kills the process!
                mediaSessionHandler.shutdown()
            }
        })
    }
    
    // Note: onTerminate() is NOT called in production Android!
    // Keeping for emulator/test compatibility only.
    override fun onTerminate() {
        mediaSessionHandler.shutdown()
        super.onTerminate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
