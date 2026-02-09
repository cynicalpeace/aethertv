package com.aethertv.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.MockDataProvider
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.domain.model.Channel
import com.aethertv.domain.model.EpgProgram
import com.aethertv.domain.usecase.GetChannelsUseCase
import com.aethertv.domain.usecase.GetEpgUseCase
import com.aethertv.data.repository.ChannelRepository
import com.aethertv.epg.EpgMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

data class ChannelWithEpg(
    val channel: Channel,
    val currentProgram: EpgProgram? = null,
    val nextProgram: EpgProgram? = null,
)

data class CategoryRow(
    val categoryName: String,
    val channels: List<ChannelWithEpg>,
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
    private val getEpgUseCase: GetEpgUseCase,
    private val epgMatcher: EpgMatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // EPG match cache - persisted across flow emissions to avoid repeated O(N*M) matching (C17 fix)
    // Key: channel name (normalized), Value: matched EPG channel ID or null if no match
    private val epgMatchCache = mutableMapOf<String, String?>()
    // Track which EPG channels we last matched against to invalidate cache on EPG data change
    private var lastEpgChannelCount = 0

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        viewModelScope.launch {
            try {
                observeChannels()
            } catch (e: Exception) {
                Log.e(TAG, "Error observing channels", e)
                _uiState.value = HomeUiState(
                    isLoading = false,
                    engineStatus = EngineStatus.NOT_FOUND,
                )
            }
        }
    }
    
    private suspend fun observeChannels() {
            // Observe channels with EPG data using type-safe combine
            // We use nested combine calls to maintain type safety
            val channelsAndCategoriesFlow = combine(
                getChannelsUseCase.all(),
                getChannelsUseCase.categories(),
            ) { channels, categories -> channels to categories }
            
            val epgFlow = combine(
                getEpgUseCase.observeAllEpgChannels(),
                getEpgUseCase.observeProgramsInRange(
                    System.currentTimeMillis() - 3600_000,
                    System.currentTimeMillis() + 7200_000,
                ),
            ) { epgChannels, programs -> epgChannels to programs }
            
            combine(
                channelsAndCategoriesFlow,
                watchHistoryDao.observeRecent(10),
                epgFlow,
            ) { channelsAndCategories, recentHistory, epgData ->
                val (allChannels, categories) = channelsAndCategories
                val (epgChannels, allPrograms) = epgData
                
                val now = System.currentTimeMillis()
                val channelMap = allChannels.associateBy { it.infohash }
                
                // Group programs by EPG channel ID
                val programsByEpgChannel = allPrograms.groupBy { it.channelId }
                
                // Invalidate cache if EPG channels changed (C17 fix)
                if (epgChannels.size != lastEpgChannelCount) {
                    epgMatchCache.clear()
                    lastEpgChannelCount = epgChannels.size
                }
                
                fun withEpg(channel: Channel): ChannelWithEpg {
                    // Find matching EPG channel - use class-level cache (C17 fix)
                    val epgChannelId = channel.epgChannelId ?: epgMatchCache.getOrPut(channel.name) {
                        epgMatcher.findBestMatchByName(channel.name, epgChannels)?.xmltvId
                    }
                    
                    if (epgChannelId != null) {
                        val programs = programsByEpgChannel[epgChannelId] ?: emptyList()
                        val current = programs.find { now in it.startTime until it.endTime }
                        val next = programs.filter { it.startTime > now }.minByOrNull { it.startTime }
                        return ChannelWithEpg(channel, current, next)
                    }
                    return ChannelWithEpg(channel)
                }
                
                // Get recently watched channels (deduplicated, in order)
                val recentInfohashes = recentHistory
                    .map { it.infohash }
                    .distinct()
                    .take(10)
                val recentChannels = recentInfohashes.mapNotNull { channelMap[it]?.let { ch -> withEpg(ch) } }
                
                val favorites = allChannels.filter { it.isFavorite }.map { withEpg(it) }
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
                    val channelsInCategory = allChannels
                        .filter { category in it.categories }
                        .map { withEpg(it) }
                    if (channelsInCategory.isNotEmpty()) {
                        rows.add(CategoryRow(category.replaceFirstChar { it.uppercase() }, channelsInCategory))
                    }
                }
                
                val status = when {
                    rows.isEmpty() -> EngineStatus.NOT_FOUND
                    rows.any { it.channels.any { ch -> ch.channel.categories.contains("mock") } } -> EngineStatus.MOCK_DATA
                    else -> EngineStatus.CONNECTED
                }
                HomeUiState(
                    categoryRows = rows,
                    isLoading = false,
                    engineStatus = status,
                )
            }.catch { e ->
                Log.e(TAG, "Error in channel flow", e)
                emit(HomeUiState(isLoading = false, engineStatus = EngineStatus.NOT_FOUND))
            }.collect { _uiState.value = it }
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
