package com.aethertv.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethertv.data.local.ChannelDao
import com.aethertv.data.local.FilterRuleDao
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.local.entity.FilterRuleEntity
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.repository.EpgRepository
import com.aethertv.data.repository.GitHubRelease
import com.aethertv.data.repository.UpdateRepository
import com.aethertv.data.repository.UpdateState
import com.aethertv.engine.AceStreamEngine
import com.aethertv.engine.StreamEngine
import com.aethertv.epg.XmltvParser
import com.aethertv.verification.StreamVerifier
import com.aethertv.verification.VerificationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

data class VerificationProgress(
    val current: Int = 0,
    val total: Int = 0,
    val liveCount: Int = 0,
    val isRunning: Boolean = false
)

data class EngineState(
    val name: String = "Unknown",
    val version: String? = null,
    val isInstalled: Boolean = false,
    val isRunning: Boolean = false
)

data class EpgSyncState(
    val url: String = "",
    val lastSync: Long = 0L,
    val isSyncing: Boolean = false,
    val channelCount: Int = 0,
    val programCount: Int = 0,
    val error: String? = null
)

data class EpgMatchState(
    val totalChannels: Int = 0,
    val matchedChannels: Int = 0,
    val availableEpgChannels: Int = 0,
    val isMatching: Boolean = false,
    val matchProgress: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updateRepository: UpdateRepository,
    private val watchHistoryDao: WatchHistoryDao,
    private val streamVerifier: StreamVerifier,
    private val channelDao: ChannelDao,
    private val streamEngine: StreamEngine,
    private val aceStreamEngine: AceStreamEngine,
    private val settingsDataStore: SettingsDataStore,
    private val epgRepository: EpgRepository,
    private val xmltvParser: XmltvParser,
    private val filterRuleDao: FilterRuleDao,
) : ViewModel() {
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()
    
    private val _currentVersion = MutableStateFlow("Loading...")
    val currentVersion: StateFlow<String> = _currentVersion.asStateFlow()
    
    private val _dataMessage = MutableStateFlow<String?>(null)
    val dataMessage: StateFlow<String?> = _dataMessage.asStateFlow()
    
    private val _verificationProgress = MutableStateFlow(VerificationProgress())
    val verificationProgress: StateFlow<VerificationProgress> = _verificationProgress.asStateFlow()
    
    private val _engineState = MutableStateFlow(EngineState())
    val engineState: StateFlow<EngineState> = _engineState.asStateFlow()
    
    private val _epgState = MutableStateFlow(EpgSyncState())
    val epgState: StateFlow<EpgSyncState> = _epgState.asStateFlow()
    
    val filterRules: StateFlow<List<FilterRuleEntity>> = filterRuleDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private var pendingRelease: GitHubRelease? = null
    private var pendingApkFile: File? = null
    private var verificationJob: Job? = null
    private var epgSyncJob: Job? = null
    
    companion object {
        private const val TAG = "SettingsViewModel"
    }
    
    init {
        _currentVersion.value = updateRepository.getCurrentVersion()
        refreshEngineStatus()
        loadEpgSettings()
    }
    
    private fun loadEpgSettings() {
        viewModelScope.launch {
            val url = settingsDataStore.epgUrl.first()
            val lastSync = settingsDataStore.epgLastSync.first()
            _epgState.value = _epgState.value.copy(
                url = url,
                lastSync = lastSync
            )
        }
    }
    
    fun updateEpgUrl(url: String) {
        _epgState.value = _epgState.value.copy(url = url, error = null)
        viewModelScope.launch {
            settingsDataStore.setEpgUrl(url)
        }
    }
    
    fun syncEpg() {
        val url = _epgState.value.url
        if (url.isBlank()) {
            _epgState.value = _epgState.value.copy(error = "Please enter an EPG URL")
            return
        }
        
        if (_epgState.value.isSyncing) return
        
        epgSyncJob = viewModelScope.launch {
            _epgState.value = _epgState.value.copy(
                isSyncing = true,
                error = null,
                channelCount = 0,
                programCount = 0
            )
            
            try {
                var channelCount = 0
                var programCount = 0
                
                withContext(Dispatchers.IO) {
                    val connection = URL(url).openConnection()
                    connection.connectTimeout = 30_000
                    connection.readTimeout = 60_000
                    
                    connection.getInputStream().use { inputStream ->
                        // Clear existing EPG data
                        epgRepository.clearAll()
                        
                        xmltvParser.parse(
                            inputStream = inputStream,
                            onChannel = { channel ->
                                viewModelScope.launch(Dispatchers.IO) {
                                    epgRepository.insertChannels(listOf(channel))
                                    channelCount++
                                    _epgState.value = _epgState.value.copy(channelCount = channelCount)
                                }
                            },
                            onProgram = { program ->
                                viewModelScope.launch(Dispatchers.IO) {
                                    epgRepository.insertPrograms(listOf(program))
                                    programCount++
                                    if (programCount % 100 == 0) {
                                        _epgState.value = _epgState.value.copy(programCount = programCount)
                                    }
                                }
                            }
                        )
                    }
                }
                
                val now = System.currentTimeMillis()
                settingsDataStore.setEpgLastSync(now)
                
                _epgState.value = _epgState.value.copy(
                    isSyncing = false,
                    lastSync = now,
                    channelCount = channelCount,
                    programCount = programCount
                )
                
                _dataMessage.value = "EPG synced: $channelCount channels, $programCount programs"
                
            } catch (e: Exception) {
                Log.e(TAG, "EPG sync failed", e)
                _epgState.value = _epgState.value.copy(
                    isSyncing = false,
                    error = e.message ?: "Sync failed"
                )
            }
        }
    }
    
    fun cancelEpgSync() {
        epgSyncJob?.cancel()
        _epgState.value = _epgState.value.copy(isSyncing = false)
    }
    
    fun clearEpg() {
        viewModelScope.launch {
            epgRepository.clearAll()
            settingsDataStore.setEpgLastSync(0L)
            _epgState.value = _epgState.value.copy(
                lastSync = 0L,
                channelCount = 0,
                programCount = 0
            )
            _dataMessage.value = "EPG data cleared"
        }
    }
    
    fun refreshEngineStatus() {
        viewModelScope.launch {
            val info = streamEngine.getEngineInfo()
            val isAvailable = streamEngine.isAvailable()
            
            _engineState.value = EngineState(
                name = info.name,
                version = info.version,
                isInstalled = aceStreamEngine.isInstalled(),
                isRunning = isAvailable
            )
        }
    }
    
    fun launchEngine() {
        aceStreamEngine.launchEngine()
        viewModelScope.launch {
            delay(3000) // Wait for engine to start
            refreshEngineStatus()
        }
    }
    
    fun installEngine() {
        aceStreamEngine.openPlayStore()
    }
    
    fun clearWatchHistory() {
        viewModelScope.launch {
            watchHistoryDao.deleteAll()
            _dataMessage.value = "Watch history cleared"
        }
    }
    
    fun dismissDataMessage() {
        _dataMessage.value = null
    }
    
    fun startVerification() {
        if (_verificationProgress.value.isRunning) return
        
        verificationJob = viewModelScope.launch {
            var liveCount = 0
            val channels = channelDao.observeAll().first()
            val total = channels.size
            
            _verificationProgress.value = VerificationProgress(
                current = 0,
                total = total,
                isRunning = true
            )
            
            channels.forEachIndexed { index, channel ->
                if (!_verificationProgress.value.isRunning) return@forEachIndexed
                
                val result = streamVerifier.verify(channel.infohash)
                
                when (result) {
                    is VerificationResult.Alive -> {
                        liveCount++
                        channelDao.updateVerification(
                            infohash = channel.infohash,
                            isVerified = true,
                            quality = result.quality.label,
                            verifiedAt = System.currentTimeMillis(),
                            peerCount = result.peers
                        )
                    }
                    is VerificationResult.Dead -> {
                        channelDao.updateVerification(
                            infohash = channel.infohash,
                            isVerified = false,
                            quality = null,
                            verifiedAt = System.currentTimeMillis(),
                            peerCount = 0
                        )
                    }
                    is VerificationResult.Error -> {
                        // Keep existing verification state on error
                    }
                }
                
                _verificationProgress.value = VerificationProgress(
                    current = index + 1,
                    total = total,
                    liveCount = liveCount,
                    isRunning = true
                )
            }
            
            _verificationProgress.value = _verificationProgress.value.copy(isRunning = false)
            _dataMessage.value = "Verification complete: $liveCount live channels"
        }
    }
    
    fun stopVerification() {
        verificationJob?.cancel()
        _verificationProgress.value = _verificationProgress.value.copy(isRunning = false)
    }
    
    fun checkForUpdate() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            val result = updateRepository.checkForUpdate()
            _updateState.value = result
            
            if (result is UpdateState.Available) {
                pendingRelease = result.release
            }
        }
    }
    
    fun downloadUpdate() {
        val release = pendingRelease ?: return
        
        viewModelScope.launch {
            updateRepository.downloadUpdate(release).collect { state ->
                _updateState.value = state
                
                if (state is UpdateState.ReadyToInstall) {
                    pendingApkFile = state.apkFile
                }
            }
        }
    }
    
    fun installUpdate() {
        pendingApkFile?.let { apkFile ->
            updateRepository.installApk(apkFile)
        }
    }
    
    fun dismissUpdate() {
        _updateState.value = UpdateState.Idle
        pendingRelease = null
        pendingApkFile = null
    }
    
    // EPG Matching
    private val _epgMatchState = MutableStateFlow(EpgMatchState())
    val epgMatchState: StateFlow<EpgMatchState> = _epgMatchState.asStateFlow()
    
    fun loadEpgMatchData() {
        viewModelScope.launch {
            val channels = channelDao.observeAll().first()
            val epgChannels = epgRepository.observeAllEpgChannels().first()
            
            val unmatchedCount = channels.count { it.epgChannelId == null }
            val matchedCount = channels.size - unmatchedCount
            
            _epgMatchState.value = EpgMatchState(
                totalChannels = channels.size,
                matchedChannels = matchedCount,
                availableEpgChannels = epgChannels.size
            )
        }
    }
    
    fun autoMatchEpg() {
        viewModelScope.launch {
            _epgMatchState.value = _epgMatchState.value.copy(isMatching = true, matchProgress = 0)
            
            val channels = channelDao.observeAll().first()
            val epgChannels = epgRepository.observeAllEpgChannels().first()
            
            var matched = 0
            channels.forEachIndexed { index, channel ->
                if (channel.epgChannelId == null) {
                    val match = com.aethertv.epg.EpgMatcher().findBestMatchByName(channel.name, epgChannels)
                    if (match != null) {
                        channelDao.updateEpgMapping(channel.infohash, match.xmltvId)
                        matched++
                    }
                }
                _epgMatchState.value = _epgMatchState.value.copy(
                    matchProgress = ((index + 1) * 100) / channels.size
                )
            }
            
            _epgMatchState.value = _epgMatchState.value.copy(isMatching = false)
            loadEpgMatchData()
            _dataMessage.value = "Auto-matched $matched channels to EPG"
        }
    }
    
    fun clearEpgMappings() {
        viewModelScope.launch {
            val channels = channelDao.observeAll().first()
            channels.forEach { channel ->
                channelDao.updateEpgMapping(channel.infohash, null)
            }
            loadEpgMatchData()
            _dataMessage.value = "EPG mappings cleared"
        }
    }
    
    // Filter rule management
    fun addFilterRule(type: String, pattern: String) {
        if (pattern.isBlank()) return
        viewModelScope.launch {
            filterRuleDao.insert(
                FilterRuleEntity(type = type, pattern = pattern.trim(), isEnabled = true)
            )
            _dataMessage.value = "Filter rule added"
        }
    }
    
    fun toggleFilterRule(rule: FilterRuleEntity) {
        viewModelScope.launch {
            filterRuleDao.setEnabled(rule.id, !rule.isEnabled)
        }
    }
    
    fun deleteFilterRule(rule: FilterRuleEntity) {
        viewModelScope.launch {
            filterRuleDao.delete(rule)
            _dataMessage.value = "Filter rule deleted"
        }
    }
}
