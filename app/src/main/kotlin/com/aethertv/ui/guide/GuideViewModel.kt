package com.aethertv.ui.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.repository.ChannelRepository
import com.aethertv.domain.model.Channel
import com.aethertv.domain.model.EpgProgram
import com.aethertv.domain.usecase.GetEpgUseCase
import com.aethertv.epg.EpgMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuideChannel(
    val channel: Channel,
    val epgChannelId: String?,
    val programs: List<EpgProgram> = emptyList(),
)

data class GuideUiState(
    val isLoading: Boolean = true,
    val channels: List<GuideChannel> = emptyList(),
    val timelineStart: Long = 0L,
    val timelineEnd: Long = 0L,
    val currentTime: Long = System.currentTimeMillis(),
    val focusedChannelIndex: Int = 0,
    val focusedProgramIndex: Int = 0,
    val selectedProgram: EpgProgram? = null,
    val hasEpgData: Boolean = false,
    val epgSourceUrl: String? = null,
)

@HiltViewModel
class GuideViewModel @Inject constructor(
    private val getEpgUseCase: GetEpgUseCase,
    private val channelRepository: ChannelRepository,
    private val epgMatcher: EpgMatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    init {
        loadGuideData()
    }

    private fun loadGuideData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val now = System.currentTimeMillis()
            // Show 4 hours: 1 hour before now, 3 hours after
            val timelineStart = now - (60 * 60 * 1000L)
            val timelineEnd = now + (3 * 60 * 60 * 1000L)

            try {
                // Get all channels
                val channels = channelRepository.observeAll().first()
                
                // Get EPG channels for matching
                val epgChannels = getEpgUseCase.observeAllEpgChannels().first()
                
                // Get programs in range
                val allPrograms = getEpgUseCase.observeProgramsInRange(timelineStart, timelineEnd).first()
                
                // Group programs by channel
                val programsByChannel = allPrograms.groupBy { it.channelId }
                
                // Build guide channels with matched EPG data
                val guideChannels = channels.map { channel: Channel ->
                    val matchedEpgChannel = findEpgMatch(channel, epgChannels)
                    val programs = matchedEpgChannel?.let { epgCh: EpgChannelEntity -> 
                        programsByChannel[epgCh.xmltvId] ?: emptyList()
                    } ?: emptyList()
                    
                    GuideChannel(
                        channel = channel,
                        epgChannelId = matchedEpgChannel?.xmltvId,
                        programs = programs.sortedBy { prog: EpgProgram -> prog.startTime },
                    )
                }.sortedByDescending { gc: GuideChannel -> gc.programs.isNotEmpty() } // Channels with EPG first
                
                _uiState.update { state: GuideUiState ->
                    state.copy(
                        isLoading = false,
                        channels = guideChannels,
                        timelineStart = timelineStart,
                        timelineEnd = timelineEnd,
                        currentTime = now,
                        hasEpgData = allPrograms.isNotEmpty(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state: GuideUiState -> state.copy(isLoading = false) }
            }
        }
    }

    private fun findEpgMatch(
        channel: Channel,
        epgChannels: List<EpgChannelEntity>,
    ): EpgChannelEntity? {
        // First try existing mapping
        channel.epgChannelId?.let { epgId ->
            return epgChannels.find { it.xmltvId == epgId }
        }
        // Fall back to fuzzy matching
        return epgMatcher.findBestMatchByName(channel.name, epgChannels)
    }

    fun selectProgram(program: EpgProgram) {
        _uiState.update { it.copy(selectedProgram = program) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedProgram = null) }
    }

    fun moveFocus(channelDelta: Int, programDelta: Int) {
        _uiState.update { state ->
            val newChannelIndex = (state.focusedChannelIndex + channelDelta)
                .coerceIn(0, (state.channels.size - 1).coerceAtLeast(0))
            
            val channel = state.channels.getOrNull(newChannelIndex)
            val maxProgram = (channel?.programs?.size ?: 1) - 1
            val newProgramIndex = (state.focusedProgramIndex + programDelta)
                .coerceIn(0, maxProgram.coerceAtLeast(0))
            
            state.copy(
                focusedChannelIndex = newChannelIndex,
                focusedProgramIndex = newProgramIndex,
            )
        }
    }

    fun refreshCurrentTime() {
        _uiState.update { it.copy(currentTime = System.currentTimeMillis()) }
    }

    fun refresh() {
        loadGuideData()
    }
}
