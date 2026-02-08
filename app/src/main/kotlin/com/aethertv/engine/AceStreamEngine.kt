package com.aethertv.engine

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.aethertv.data.remote.AceStreamEngineClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

/**
 * AceStream-based implementation of StreamEngine.
 * 
 * Requires the AceStream Engine app to be installed separately.
 * Communicates via HTTP API at localhost:6878.
 */
@Singleton
class AceStreamEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val engineClient: AceStreamEngineClient,
) : StreamEngine {

    companion object {
        private const val TAG = "AceStreamEngine"
        
        // AceStream package names (main app and engine variants)
        private val ACESTREAM_PACKAGES = listOf(
            "org.acestream.media",
            "org.acestream.core",
            "org.acestream.engine"
        )
        
        // Play Store URL for installation
        private const val PLAY_STORE_URL = "market://details?id=org.acestream.media"
        private const val PLAY_STORE_WEB_URL = "https://play.google.com/store/apps/details?id=org.acestream.media"
        
        // APK download URL (fallback for TV devices without Play Store)
        private const val APK_DOWNLOAD_URL = "https://download.acestream.media/android/latest"
    }
    
    private val _status = MutableStateFlow<EngineStatus>(EngineStatus.Unknown)
    
    override suspend fun isAvailable(): Boolean {
        return try {
            engineClient.waitForConnection(timeout = 5.seconds)
            _status.value = EngineStatus.Ready
            true
        } catch (e: Exception) {
            Log.d(TAG, "Engine not available: ${e.message}")
            _status.value = if (isInstalled()) EngineStatus.Installed else EngineStatus.NotInstalled
            false
        }
    }
    
    override fun observeStatus(): Flow<EngineStatus> = _status.asStateFlow()
    
    override suspend fun searchChannels(): List<EngineChannel> {
        return try {
            engineClient.searchAll().map { channel ->
                EngineChannel(
                    id = channel.infohash,
                    name = channel.name,
                    categories = channel.categories,
                    languages = channel.languages,
                    countries = channel.countries,
                    iconUrl = channel.iconUrl,
                    metadata = mapOf(
                        "status" to channel.status,
                        "availability" to channel.availability
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to search channels", e)
            emptyList()
        }
    }
    
    override suspend fun requestStream(contentId: String): StreamSession {
        val streamInfo = engineClient.requestStream(contentId)
        
        return StreamSession(
            contentId = contentId,
            playbackUrl = streamInfo.playbackUrl,
            statsUrl = streamInfo.statUrl,
            stopSession = {
                try {
                    engineClient.stopStream(streamInfo.commandUrl)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to stop stream: ${e.message}")
                }
            }
        )
    }
    
    override fun getEngineInfo(): EngineInfo {
        val installedPackage = getInstalledPackage()
        val version = installedPackage?.let { getPackageVersion(it) }
        
        return EngineInfo(
            name = "AceStream",
            version = version,
            isEmbedded = false,
            requiresExternalApp = true
        )
    }
    
    override suspend fun ensureInstalled(): InstallResult {
        if (isInstalled()) {
            // Try to start the engine if not running
            if (!isAvailable()) {
                launchEngine()
                delay(3.seconds)
                if (isAvailable()) {
                    return InstallResult.AlreadyInstalled
                }
            }
            return InstallResult.AlreadyInstalled
        }
        
        // Not installed - return that installation is required
        // The UI should handle showing installation instructions
        return InstallResult.InstallationRequired
    }
    
    /**
     * Check if AceStream is installed.
     */
    fun isInstalled(): Boolean = getInstalledPackage() != null
    
    /**
     * Get the installed AceStream package name.
     */
    private fun getInstalledPackage(): String? {
        val pm = context.packageManager
        for (packageName in ACESTREAM_PACKAGES) {
            try {
                pm.getPackageInfo(packageName, 0)
                return packageName
            } catch (_: PackageManager.NameNotFoundException) {
                // Not installed
            }
        }
        return null
    }
    
    /**
     * Get the version of an installed package.
     */
    private fun getPackageVersion(packageName: String): String? {
        return try {
            context.packageManager.getPackageInfo(packageName, 0).versionName
        } catch (_: Exception) {
            null
        }
    }
    
    /**
     * Launch the AceStream engine via Intent.
     */
    fun launchEngine() {
        val packageName = getInstalledPackage() ?: return
        
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch engine", e)
        }
    }
    
    /**
     * Open Play Store to install AceStream.
     */
    fun openPlayStore() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Play Store not available, try web URL
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_WEB_URL))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to open Play Store", e2)
            }
        }
    }
    
    /**
     * Get the APK download URL for manual installation.
     */
    fun getApkDownloadUrl(): String = APK_DOWNLOAD_URL
}
