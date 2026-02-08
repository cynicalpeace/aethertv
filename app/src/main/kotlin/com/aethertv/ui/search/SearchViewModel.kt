package com.aethertv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.domain.model.Channel
import com.aethertv.domain.usecase.SearchChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Channel> = emptyList(),
    val isSearching: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchChannelsUseCase: SearchChannelsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), isSearching = false)
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            delay(300) // debounce
            searchChannelsUseCase(query).collect { results ->
                _uiState.value = _uiState.value.copy(results = results, isSearching = false)
            }
        }
    }
    
    // Alias for compatibility
    fun onQueryChanged(query: String) = updateQuery(query)
}
