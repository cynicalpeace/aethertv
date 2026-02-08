package com.aethertv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.repository.GitHubRelease
import com.aethertv.data.repository.UpdateRepository
import com.aethertv.data.repository.UpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updateRepository: UpdateRepository,
    private val watchHistoryDao: WatchHistoryDao,
) : ViewModel() {
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()
    
    private val _currentVersion = MutableStateFlow("Loading...")
    val currentVersion: StateFlow<String> = _currentVersion.asStateFlow()
    
    private val _dataMessage = MutableStateFlow<String?>(null)
    val dataMessage: StateFlow<String?> = _dataMessage.asStateFlow()
    
    private var pendingRelease: GitHubRelease? = null
    private var pendingApkFile: File? = null
    
    init {
        _currentVersion.value = updateRepository.getCurrentVersion()
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
