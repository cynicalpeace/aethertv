package com.aethertv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.data.repository.ChannelRepository
import com.aethertv.domain.model.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val channel: Channel? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val engineClient: AceStreamEngineClient,
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun loadChannel(infohash: String) {
        viewModelScope.launch {
            try {
                val channel = channelRepository.getByInfohash(infohash)
                _uiState.value = _uiState.value.copy(channel = channel)
                val streamInfo = engineClient.requestStream(infohash)
                val mediaItem = androidx.media3.common.MediaItem.fromUri(streamInfo.playbackUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load stream",
                )
            }
        }
    }

    fun releasePlayer() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
