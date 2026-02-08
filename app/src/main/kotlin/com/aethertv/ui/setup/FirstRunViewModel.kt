package com.aethertv.ui.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.MockDataProvider
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

data class FirstRunUiState(
    val step: SetupStep = SetupStep.WELCOME,
    val channelsFound: Int = 0,
    val statusMessage: String = "",
)

@HiltViewModel
class FirstRunViewModel @Inject constructor(
    private val aceStreamClient: AceStreamEngineClient,
    private val channelRepository: ChannelRepository,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirstRunUiState())
    val uiState: StateFlow<FirstRunUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "FirstRunViewModel"
    }

    fun startScanning() {
        viewModelScope.launch {
            _uiState.value = FirstRunUiState(
                step = SetupStep.SCANNING,
                statusMessage = "Connecting to AceStream Engine..."
            )

            try {
                // Try to connect to AceStream
                Log.d(TAG, "Attempting to connect to AceStream...")
                aceStreamClient.waitForConnection(timeout = 10.seconds)
                
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Fetching channel list..."
                )
                
                val channels = aceStreamClient.searchAll()
                Log.d(TAG, "Found ${channels.size} channels from AceStream")
                
                if (channels.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        channelsFound = channels.size,
                        statusMessage = "Saving channels..."
                    )
                    
                    channelRepository.insertFromScraper(channels, System.currentTimeMillis())
                    
                    delay(500)
                    
                    _uiState.value = _uiState.value.copy(
                        step = SetupStep.COMPLETE
                    )
                } else {
                    // No channels from AceStream, use mock data
                    loadMockData()
                }
            } catch (e: Exception) {
                Log.d(TAG, "AceStream not available: ${e.message}")
                // Fall back to mock data
                loadMockData()
            }
            
            // Mark first run as complete
            settingsDataStore.setFirstRunComplete()
        }
    }
    
    private suspend fun loadMockData() {
        _uiState.value = _uiState.value.copy(
            statusMessage = "AceStream not found, loading demo channels..."
        )
        
        delay(500)
        
        val mockChannels = MockDataProvider.getMockChannels()
        channelRepository.insertFromScraper(mockChannels, System.currentTimeMillis())
        
        _uiState.value = _uiState.value.copy(
            step = SetupStep.COMPLETE,
            channelsFound = 0 // 0 indicates mock data
        )
    }
}
