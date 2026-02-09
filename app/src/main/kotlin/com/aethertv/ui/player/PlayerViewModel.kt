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

/**
 * Tracks an active AceStream session for proper cleanup.
 */
private data class ActiveSession(
    val infohash: String,
    val commandUrl: String,
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
    
    // Track active session for proper cleanup (C10/H24 fix)
    private var activeSession: ActiveSession? = null
    
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
            // Stop any existing session first (C10 fix)
            stopCurrentSession()
            
            val streamInfo = engineClient.requestStream(infohash)
            
            // Store session for cleanup (H24 fix)
            activeSession = ActiveSession(
                infohash = infohash,
                commandUrl = streamInfo.commandUrl,
            )
            
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
            activeSession = null
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Failed to connect to stream",
            )
        }
    }
    
    /**
     * Stop the current AceStream session if one exists.
     */
    private suspend fun stopCurrentSession() {
        activeSession?.let { session ->
            try {
                engineClient.stopStream(session.commandUrl)
                Log.d(TAG, "Stopped AceStream session for ${session.infohash}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to stop AceStream session: ${e.message}")
            }
            activeSession = null
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
        // Stop AceStream session - this must complete even during ViewModel cleanup
        // We capture the session before nulling to ensure cleanup (C15 fix)
        val sessionToStop = activeSession
        activeSession = null
        
        sessionToStop?.let { session ->
            // Use kotlinx.coroutines.runBlocking for critical cleanup
            // This ensures the stream is stopped even when called from onCleared()
            // where viewModelScope is already cancelled
            try {
                kotlinx.coroutines.runBlocking {
                    kotlinx.coroutines.withTimeoutOrNull(2000L) {
                        engineClient.stopStream(session.commandUrl)
                    }
                }
                Log.d(TAG, "Stopped AceStream session on release: ${session.infohash}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to stop stream on release: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Remove listener first to prevent stale updates (H22 fix)
        exoPlayer.removeListener(playerListener)
        // releasePlayer uses runBlocking for cleanup so it works even after scope cancellation (C15 fix)
        releasePlayer()
    }
}
