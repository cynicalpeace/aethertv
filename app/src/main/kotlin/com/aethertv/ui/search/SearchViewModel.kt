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
import kotlinx.coroutines.flow.first
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

    companion object {
        private const val DEBOUNCE_MS = 300L
    }

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
            delay(DEBOUNCE_MS) // debounce
            try {
                // Use .first() instead of .collect{} to get one-shot result
                // This prevents lingering flow collection if user types again
                val results = searchChannelsUseCase(query).first()
                _uiState.value = _uiState.value.copy(results = results, isSearching = false)
            } catch (e: Exception) {
                // Handle cancellation gracefully
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(results = emptyList(), isSearching = false)
                }
            }
        }
    }
    
    // Alias for compatibility
    fun onQueryChanged(query: String) = updateQuery(query)
}
