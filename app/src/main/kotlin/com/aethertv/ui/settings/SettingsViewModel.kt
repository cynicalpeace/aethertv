package com.aethertv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.local.ChannelDao
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.repository.GitHubRelease
import com.aethertv.data.repository.UpdateRepository
import com.aethertv.data.repository.UpdateState
import com.aethertv.engine.AceStreamEngine
import com.aethertv.engine.StreamEngine
import com.aethertv.verification.StreamVerifier
import com.aethertv.verification.VerificationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class VerificationProgress(
    val current: Int = 0,
    val total: Int = 0,
    val liveCount: Int = 0,
    val isRunning: Boolean = false
)

data class EngineState(
    val name: String = "Unknown",
    val version: String? = null,
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updateRepository: UpdateRepository,
    private val watchHistoryDao: WatchHistoryDao,
    private val streamVerifier: StreamVerifier,
    private val channelDao: ChannelDao,
    private val streamEngine: StreamEngine,
    private val aceStreamEngine: AceStreamEngine,
) : ViewModel() {
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()
    
    private val _currentVersion = MutableStateFlow("Loading...")
    val currentVersion: StateFlow<String> = _currentVersion.asStateFlow()
    
    private val _dataMessage = MutableStateFlow<String?>(null)
    val dataMessage: StateFlow<String?> = _dataMessage.asStateFlow()
    
    private val _verificationProgress = MutableStateFlow(VerificationProgress())
    val verificationProgress: StateFlow<VerificationProgress> = _verificationProgress.asStateFlow()
    
    private val _engineState = MutableStateFlow(EngineState())
    val engineState: StateFlow<EngineState> = _engineState.asStateFlow()
    
    private var pendingRelease: GitHubRelease? = null
    private var pendingApkFile: File? = null
    private var verificationJob: Job? = null
    
    init {
        _currentVersion.value = updateRepository.getCurrentVersion()
        refreshEngineStatus()
    }
    
    fun refreshEngineStatus() {
        viewModelScope.launch {
            val info = streamEngine.getEngineInfo()
            val isAvailable = streamEngine.isAvailable()
            
            _engineState.value = EngineState(
                name = info.name,
                version = info.version,
                isInstalled = aceStreamEngine.isInstalled(),
                isRunning = isAvailable
            )
        }
    }
    
    fun launchEngine() {
        aceStreamEngine.launchEngine()
        viewModelScope.launch {
            delay(3000) // Wait for engine to start
            refreshEngineStatus()
        }
    }
    
    fun installEngine() {
        aceStreamEngine.openPlayStore()
    }
    
    fun clearWatchHistory() {
        viewModelScope.launch {
            watchHistoryDao.deleteAll()
            _dataMessage.value = "Watch history cleared"
        }
    }
    
    fun dismissDataMessage() {
        _dataMessage.value = null
    }
    
    fun startVerification() {
        if (_verificationProgress.value.isRunning) return
        
        verificationJob = viewModelScope.launch {
            var liveCount = 0
            val channels = channelDao.observeAll().first()
            val total = channels.size
            
            _verificationProgress.value = VerificationProgress(
                current = 0,
                total = total,
                isRunning = true
            )
            
            channels.forEachIndexed { index, channel ->
                if (!_verificationProgress.value.isRunning) return@forEachIndexed
                
                val result = streamVerifier.verify(channel.infohash)
                
                when (result) {
                    is VerificationResult.Alive -> {
                        liveCount++
                        channelDao.updateVerification(
                            infohash = channel.infohash,
                            isVerified = true,
                            quality = result.quality.label,
                            verifiedAt = System.currentTimeMillis(),
                            peerCount = result.peers
                        )
                    }
                    is VerificationResult.Dead -> {
                        channelDao.updateVerification(
                            infohash = channel.infohash,
                            isVerified = false,
                            quality = null,
                            verifiedAt = System.currentTimeMillis(),
                            peerCount = 0
                        )
                    }
                    is VerificationResult.Error -> {
                        // Keep existing verification state on error
                    }
                }
                
                _verificationProgress.value = VerificationProgress(
                    current = index + 1,
                    total = total,
                    liveCount = liveCount,
                    isRunning = true
                )
            }
            
            _verificationProgress.value = _verificationProgress.value.copy(isRunning = false)
            _dataMessage.value = "Verification complete: $liveCount live channels"
        }
    }
    
    fun stopVerification() {
        verificationJob?.cancel()
        _verificationProgress.value = _verificationProgress.value.copy(isRunning = false)
    }
    
    fun checkForUpdate() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            val result = updateRepository.checkForUpdate()
            _updateState.value = result
            
            if (result is UpdateState.Available) {
                pendingRelease = result.release
            }
        }
    }
    
    fun downloadUpdate() {
        val release = pendingRelease ?: return
        
        viewModelScope.launch {
            updateRepository.downloadUpdate(release).collect { state ->
                _updateState.value = state
                
                if (state is UpdateState.ReadyToInstall) {
                    pendingApkFile = state.apkFile
                }
            }
        }
    }
    
    fun installUpdate() {
        pendingApkFile?.let { apkFile ->
            updateRepository.installApk(apkFile)
        }
    }
    
    fun dismissUpdate() {
        _updateState.value = UpdateState.Idle
        pendingRelease = null
        pendingApkFile = null
    }
}
