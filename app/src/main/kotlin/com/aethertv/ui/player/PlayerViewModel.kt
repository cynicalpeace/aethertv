package com.aethertv.ui.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.local.entity.WatchHistoryEntity
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.data.repository.ChannelRepository
import com.aethertv.domain.model.Channel
import com.aethertv.domain.usecase.GetChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val channel: Channel? = null,
    val isLoading: Boolean = true,
    val isBuffering: Boolean = false,
    val error: String? = null,
    val channelList: List<Channel> = emptyList(),
    val currentIndex: Int = -1,
    val bufferPercent: Int = 0,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val engineClient: AceStreamEngineClient,
    private val channelRepository: ChannelRepository,
    private val getChannelsUseCase: GetChannelsUseCase,
    private val watchHistoryDao: WatchHistoryDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "PlayerViewModel"
    }
    
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val isBuffering = playbackState == Player.STATE_BUFFERING
            _uiState.value = _uiState.value.copy(
                isBuffering = isBuffering,
                bufferPercent = exoPlayer.bufferedPercentage
            )
        }
        
        override fun onIsLoadingChanged(isLoading: Boolean) {
            _uiState.value = _uiState.value.copy(
                bufferPercent = exoPlayer.bufferedPercentage
            )
        }
    }
    
    init {
        exoPlayer.addListener(playerListener)
    }

    fun loadChannel(infohash: String) {
        viewModelScope.launch {
            try {
                // Load channel list for navigation
                val allChannels = getChannelsUseCase.all().first()
                val currentIndex = allChannels.indexOfFirst { it.infohash == infohash }
                
                val channel = channelRepository.getByInfohash(infohash)
                _uiState.value = _uiState.value.copy(
                    channel = channel,
                    channelList = allChannels,
                    currentIndex = currentIndex,
                    isLoading = true,
                    error = null
                )
                
                startStream(infohash)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load channel", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load stream",
                )
            }
        }
    }
    
    private suspend fun startStream(infohash: String) {
        try {
            val streamInfo = engineClient.requestStream(infohash)
            val mediaItem = androidx.media3.common.MediaItem.fromUri(streamInfo.playbackUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            
            // Record to watch history
            watchHistoryDao.insert(
                WatchHistoryEntity(
                    infohash = infohash,
                    watchedAt = System.currentTimeMillis(),
                    durationSeconds = 0 // We don't track duration yet
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start stream", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Failed to connect to stream",
            )
        }
    }
    
    fun nextChannel(): Boolean {
        val state = _uiState.value
        if (state.channelList.isEmpty()) return false
        
        val nextIndex = (state.currentIndex + 1) % state.channelList.size
        val nextChannel = state.channelList[nextIndex]
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                channel = nextChannel,
                currentIndex = nextIndex,
                isLoading = true,
                error = null
            )
            exoPlayer.stop()
            startStream(nextChannel.infohash)
        }
        return true
    }
    
    fun previousChannel(): Boolean {
        val state = _uiState.value
        if (state.channelList.isEmpty()) return false
        
        val prevIndex = if (state.currentIndex <= 0) state.channelList.size - 1 else state.currentIndex - 1
        val prevChannel = state.channelList[prevIndex]
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                channel = prevChannel,
                currentIndex = prevIndex,
                isLoading = true,
                error = null
            )
            exoPlayer.stop()
            startStream(prevChannel.infohash)
        }
        return true
    }

    fun getPlayer(): ExoPlayer = exoPlayer
    
    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }
    
    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun releasePlayer() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(playerListener)
        releasePlayer()
    }
}
