package com.aethertv.ui.setup

import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FirstRunUiState(
    val step: SetupStep = SetupStep.WELCOME,
    val channelsFound: Int = 0,
    val statusMessage: String = "",
    val engineInstalled: Boolean = true,
    val engineInfo: String = "",
)

@HiltViewModel
class FirstRunViewModel @Inject constructor(
    private val streamEngine: StreamEngine,
    private val aceStreamEngine: AceStreamEngine, // For install helpers
    private val channelRepository: ChannelRepository,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirstRunUiState())
    val uiState: StateFlow<FirstRunUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "FirstRunViewModel"
    }
    
    init {
        // Check engine status on init
        val info = streamEngine.getEngineInfo()
        _uiState.value = _uiState.value.copy(
            engineInfo = "${info.name} ${info.version ?: "(not installed)"}"
        )
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                step = SetupStep.SCANNING,
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
                    // Continue with mock data
                    loadMockData()
                    return@launch
                }
                is InstallResult.Failed -> {
                    Log.e(TAG, "Engine installation failed: ${installResult.reason}")
                    loadMockData()
                    return@launch
                }
                else -> {
                    // Engine is available
                }
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
                    
                    // Convert to AceStreamChannel format for repository
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
            
            // Mark first run as complete
            settingsDataStore.setFirstRunComplete()
        }
    }
    
    fun openEngineInstall() {
        aceStreamEngine.openPlayStore()
    }
    
    fun retryWithEngine() {
        _uiState.value = _uiState.value.copy(
            step = SetupStep.WELCOME,
            engineInstalled = true
        )
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
            channelsFound = 0 // 0 indicates mock data
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
