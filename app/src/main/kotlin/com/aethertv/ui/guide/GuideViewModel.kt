package com.aethertv.ui.guide

import androidx.lifecycle.ViewModel
import com.aethertv.domain.usecase.GetEpgUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class GuideUiState(
    val isLoading: Boolean = true,
)

@HiltViewModel
class GuideViewModel @Inject constructor(
    private val getEpgUseCase: GetEpgUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()
}
