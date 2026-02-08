package com.aethertv.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.MockDataProvider
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.domain.model.Channel
import com.aethertv.domain.usecase.GetChannelsUseCase
import com.aethertv.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

data class CategoryRow(
    val categoryName: String,
    val channels: List<Channel>,
)

data class HomeUiState(
    val categoryRows: List<CategoryRow> = emptyList(),
    val isLoading: Boolean = true,
    val engineStatus: EngineStatus = EngineStatus.UNKNOWN,
)

enum class EngineStatus {
    UNKNOWN, CONNECTED, NOT_FOUND, MOCK_DATA
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val channelRepository: ChannelRepository,
    private val settingsDataStore: SettingsDataStore,
    private val aceStreamClient: AceStreamEngineClient,
    private val watchHistoryDao: WatchHistoryDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        viewModelScope.launch {
            // Observe channels (FirstRunScreen handles initial data loading)
            combine(
                getChannelsUseCase.all(),
                getChannelsUseCase.categories(),
                watchHistoryDao.observeRecent(10),
            ) { allChannels, categories, recentHistory ->
                val channelMap = allChannels.associateBy { it.infohash }
                
                // Get recently watched channels (deduplicated, in order)
                val recentInfohashes = recentHistory
                    .map { it.infohash }
                    .distinct()
                    .take(10)
                val recentChannels = recentInfohashes.mapNotNull { channelMap[it] }
                
                val favorites = allChannels.filter { it.isFavorite }
                val rows = mutableListOf<CategoryRow>()
                
                // Add recently watched first
                if (recentChannels.isNotEmpty()) {
                    rows.add(CategoryRow("Recently Watched", recentChannels))
                }
                
                // Then favorites
                if (favorites.isNotEmpty()) {
                    rows.add(CategoryRow("Favorites", favorites))
                }
                
                // Then categories
                for (category in categories) {
                    val channelsInCategory = allChannels.filter { category in it.categories }
                    if (channelsInCategory.isNotEmpty()) {
                        rows.add(CategoryRow(category.replaceFirstChar { it.uppercase() }, channelsInCategory))
                    }
                }
                
                val status = when {
                    rows.isEmpty() -> EngineStatus.NOT_FOUND
                    rows.any { it.channels.any { ch -> ch.categories.contains("mock") } } -> EngineStatus.MOCK_DATA
                    else -> EngineStatus.CONNECTED
                }
                HomeUiState(
                    categoryRows = rows,
                    isLoading = false,
                    engineStatus = status,
                )
            }.collect { _uiState.value = it }
        }
    }

    private suspend fun tryFetchFromAceStream(): Boolean {
        return try {
            Log.d(TAG, "Attempting to connect to AceStream engine...")
            aceStreamClient.waitForConnection(timeout = 10.seconds)
            Log.d(TAG, "Connected! Fetching channels...")
            
            val channels = aceStreamClient.searchAll()
            Log.d(TAG, "Found ${channels.size} channels from AceStream")
            
            if (channels.isNotEmpty()) {
                channelRepository.insertFromScraper(channels, System.currentTimeMillis())
                _uiState.value = _uiState.value.copy(engineStatus = EngineStatus.CONNECTED)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.d(TAG, "AceStream not available: ${e.message}")
            false
        }
    }

    private suspend fun loadMockData() {
        Log.d(TAG, "Loading mock data...")
        val mockChannels = MockDataProvider.getMockChannels()
        channelRepository.insertFromScraper(mockChannels, System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(engineStatus = EngineStatus.MOCK_DATA)
    }

    fun toggleFavorite(infohash: String) {
        viewModelScope.launch {
            channelRepository.toggleFavorite(infohash)
        }
    }
    
    fun refreshChannels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val connected = tryFetchFromAceStream()
            if (!connected) {
                // Keep existing data, just update status
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    engineStatus = EngineStatus.NOT_FOUND
                )
            }
        }
    }
}
