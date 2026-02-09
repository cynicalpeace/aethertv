package com.aethertv.ui.setup

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.MockDataProvider
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.repository.ChannelRepository
import com.aethertv.engine.AceStreamEngine
import com.aethertv.engine.EngineChannel
import com.aethertv.engine.InstallResult
import com.aethertv.engine.StreamEngine
import com.aethertv.data.remote.AceStreamChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

enum class InstallState {
    IDLE,
    EXTRACTING,
    INSTALLING,
    WAITING,
    SUCCESS,
    FAILED
}

data class FirstRunUiState(
    val step: SetupStep = SetupStep.WELCOME,
    val channelsFound: Int = 0,
    val statusMessage: String = "",
    val engineInstalled: Boolean = true,
    val engineInfo: String = "",
    val installState: InstallState = InstallState.IDLE,
    val installProgress: Float = 0f,
    val installError: String? = null,
)

@HiltViewModel
class FirstRunViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val streamEngine: StreamEngine,
    private val aceStreamEngine: AceStreamEngine,
    private val channelRepository: ChannelRepository,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirstRunUiState())
    val uiState: StateFlow<FirstRunUiState> = _uiState.asStateFlow()
    
    // Intent for starting installation, observed by UI
    private val _installIntent = MutableStateFlow<Intent?>(null)
    val installIntent: StateFlow<Intent?> = _installIntent.asStateFlow()

    companion object {
        private const val TAG = "FirstRunViewModel"
        private const val ACESTREAM_PACKAGE = "org.acestream.core"
        private const val BUNDLED_APK_NAME = "acestream-engine.apk"
    }
    
    init {
        checkEngineStatus()
    }
    
    private fun checkEngineStatus() {
        val isInstalled = isAceStreamInstalled()
        val info = streamEngine.getEngineInfo()
        _uiState.value = _uiState.value.copy(
            engineInstalled = isInstalled,
            engineInfo = "${info.name} ${info.version ?: "(not installed)"}"
        )
    }
    
    private fun isAceStreamInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(ACESTREAM_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            // First check if engine is installed
            if (!isAceStreamInstalled()) {
                _uiState.value = _uiState.value.copy(
                    step = SetupStep.SCANNING,
                    engineInstalled = false,
                    statusMessage = "AceStream Engine required"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(
                step = SetupStep.SCANNING,
                engineInstalled = true,
                statusMessage = "Checking streaming engine..."
            )

            // Check if engine is installed and ready
            val installResult = streamEngine.ensureInstalled()
            
            when (installResult) {
                is InstallResult.InstallationRequired -> {
                    _uiState.value = _uiState.value.copy(
                        engineInstalled = false,
                        statusMessage = "Streaming engine not installed"
                    )
                    loadMockData()
                    return@launch
                }
                is InstallResult.Failed -> {
                    Log.e(TAG, "Engine installation failed: ${installResult.reason}")
                    loadMockData()
                    return@launch
                }
                else -> {}
            }

            try {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Connecting to streaming engine..."
                )
                
                if (!streamEngine.isAvailable()) {
                    Log.d(TAG, "Engine not available")
                    loadMockData()
                    return@launch
                }
                
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Fetching channel list..."
                )
                
                val channels = streamEngine.searchChannels()
                Log.d(TAG, "Found ${channels.size} channels")
                
                if (channels.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        channelsFound = channels.size,
                        statusMessage = "Saving channels..."
                    )
                    
                    val aceChannels = channels.map { it.toAceStreamChannel() }
                    channelRepository.insertFromScraper(aceChannels, System.currentTimeMillis())
                    
                    delay(500)
                    
                    _uiState.value = _uiState.value.copy(
                        step = SetupStep.COMPLETE
                    )
                } else {
                    loadMockData()
                }
            } catch (e: Exception) {
                Log.d(TAG, "Scan failed: ${e.message}")
                loadMockData()
            }
            
            settingsDataStore.setFirstRunComplete()
        }
    }
    
    /**
     * Start bundled APK installation flow.
     * Extracts APK from assets and triggers system installer.
     */
    fun installBundledEngine() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                installState = InstallState.EXTRACTING,
                installProgress = 0f,
                installError = null,
                statusMessage = "Preparing installation..."
            )
            
            try {
                // Extract APK from assets to cache
                val apkFile = extractApkFromAssets()
                
                if (apkFile == null) {
                    _uiState.value = _uiState.value.copy(
                        installState = InstallState.FAILED,
                        installError = "Failed to extract APK"
                    )
                    return@launch
                }
                
                _uiState.value = _uiState.value.copy(
                    installState = InstallState.INSTALLING,
                    installProgress = 0.5f,
                    statusMessage = "Starting installer..."
                )
                
                // Create install intent
                val intent = createInstallIntent(apkFile)
                _installIntent.value = intent
                
                _uiState.value = _uiState.value.copy(
                    installState = InstallState.WAITING,
                    installProgress = 0.75f,
                    statusMessage = "Waiting for installation..."
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Installation failed", e)
                _uiState.value = _uiState.value.copy(
                    installState = InstallState.FAILED,
                    installError = e.message ?: "Installation failed"
                )
            }
        }
    }
    
    /**
     * Called when returning from package installer.
     */
    fun onInstallResult() {
        _installIntent.value = null
        
        viewModelScope.launch {
            // Give system time to complete installation
            delay(500)
            
            if (isAceStreamInstalled()) {
                _uiState.value = _uiState.value.copy(
                    installState = InstallState.SUCCESS,
                    engineInstalled = true,
                    installProgress = 1f,
                    statusMessage = "Installation complete!"
                )
                
                delay(1000)
                
                // Continue with scanning
                startScanning()
            } else {
                _uiState.value = _uiState.value.copy(
                    installState = InstallState.FAILED,
                    installError = "Installation was cancelled or failed"
                )
            }
        }
    }
    
    /**
     * Extract APK from assets to cache directory.
     * Uses AssetFileDescriptor to get actual file size for accurate progress.
     * Note: input.available() does NOT return total file size! (C14 fix)
     */
    private suspend fun extractApkFromAssets(): File? {
        return try {
            val cacheDir = context.cacheDir
            val apkFile = File(cacheDir, BUNDLED_APK_NAME)
            
            // Delete if exists
            if (apkFile.exists()) {
                apkFile.delete()
            }
            
            // Get actual file size using AssetFileDescriptor (C14 fix)
            val assetFd = try {
                context.assets.openFd(BUNDLED_APK_NAME)
            } catch (e: Exception) {
                // Asset might be compressed - fall back to no-progress mode
                Log.w(TAG, "Cannot get asset file descriptor (compressed?): ${e.message}")
                null
            }
            
            val totalSize = assetFd?.length ?: -1L
            assetFd?.close()
            
            context.assets.open(BUNDLED_APK_NAME).use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    var total = 0L
                    
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        total += read
                        
                        // Only show progress if we know the total size
                        val progress = if (totalSize > 0) {
                            (total.toFloat() / totalSize) * 0.5f
                        } else {
                            // Unknown size - show indeterminate progress
                            0.25f
                        }
                        _uiState.value = _uiState.value.copy(installProgress = progress)
                    }
                }
            }
            
            apkFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract APK", e)
            null
        }
    }
    
    private fun createInstallIntent(apkFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }
    
    fun openEngineInstall() {
        aceStreamEngine.openPlayStore()
    }
    
    fun retryInstall() {
        _uiState.value = _uiState.value.copy(
            installState = InstallState.IDLE,
            installError = null
        )
    }
    
    fun skipEngine() {
        viewModelScope.launch {
            loadMockData()
        }
    }
    
    private suspend fun loadMockData() {
        _uiState.value = _uiState.value.copy(
            statusMessage = "Loading demo channels..."
        )
        
        delay(500)
        
        val mockChannels = MockDataProvider.getMockChannels()
        channelRepository.insertFromScraper(mockChannels, System.currentTimeMillis())
        
        _uiState.value = _uiState.value.copy(
            step = SetupStep.COMPLETE,
            channelsFound = 0
        )
        
        settingsDataStore.setFirstRunComplete()
    }
    
    private fun EngineChannel.toAceStreamChannel(): AceStreamChannel {
        return AceStreamChannel(
            infohash = id,
            name = name,
            categories = categories,
            languages = languages,
            countries = countries,
            icons = iconUrl?.let { listOf(com.aethertv.data.remote.ChannelIcon(it)) } ?: emptyList(),
            status = (metadata["status"] as? Int) ?: 0,
            availability = (metadata["availability"] as? Float) ?: 0f
        )
    }
}
