package com.aethertv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.MockDataProvider
import com.aethertv.data.preferences.SettingsDataStore
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Check if first run and load mock data
            val isFirstRun = settingsDataStore.isFirstRun.first()
            if (isFirstRun) {
                loadMockData()
                settingsDataStore.setFirstRunComplete()
            }
            
            // Observe channels
            combine(
                getChannelsUseCase.all(),
                getChannelsUseCase.categories(),
            ) { allChannels, categories ->
                val favorites = allChannels.filter { it.isFavorite }
                val rows = mutableListOf<CategoryRow>()
                if (favorites.isNotEmpty()) {
                    rows.add(CategoryRow("Favorites", favorites))
                }
                for (category in categories) {
                    val channelsInCategory = allChannels.filter { category in it.categories }
                    if (channelsInCategory.isNotEmpty()) {
                        rows.add(CategoryRow(category.replaceFirstChar { it.uppercase() }, channelsInCategory))
                    }
                }
                HomeUiState(
                    categoryRows = rows,
                    isLoading = false,
                    engineStatus = if (rows.isNotEmpty()) EngineStatus.MOCK_DATA else EngineStatus.NOT_FOUND,
                )
            }.collect { _uiState.value = it }
        }
    }

    private suspend fun loadMockData() {
        val mockChannels = MockDataProvider.getMockChannels()
        channelRepository.insertFromScraper(mockChannels, System.currentTimeMillis())
    }

    fun toggleFavorite(infohash: String) {
        viewModelScope.launch {
            channelRepository.toggleFavorite(infohash)
        }
    }
    
    fun refreshChannels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadMockData()
        }
    }
}
