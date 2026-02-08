package com.aethertv.engine

import kotlinx.coroutines.flow.Flow

/**
 * Abstract interface for P2P streaming engines.
 * 
 * Current implementation: AceStreamEngine (external app)
 * Future options: LibTorrentEngine, embedded SDK, etc.
 */
interface StreamEngine {
    
    /**
     * Check if the engine is available and ready to use.
     */
    suspend fun isAvailable(): Boolean
    
    /**
     * Get the current engine status.
     */
    fun observeStatus(): Flow<EngineStatus>
    
    /**
     * Search for available channels.
     */
    suspend fun searchChannels(): List<EngineChannel>
    
    /**
     * Request a playback URL for a content hash.
     * Returns a StreamSession that must be stopped when done.
     */
    suspend fun requestStream(contentId: String): StreamSession
    
    /**
     * Get engine info (name, version, etc.)
     */
    fun getEngineInfo(): EngineInfo
    
    /**
     * Install or update the engine if needed.
     * Returns true if installation was successful or not needed.
     */
    suspend fun ensureInstalled(): InstallResult
}

data class EngineInfo(
    val name: String,
    val version: String?,
    val isEmbedded: Boolean,
    val requiresExternalApp: Boolean
)

sealed class EngineStatus {
    object Unknown : EngineStatus()
    object NotInstalled : EngineStatus()
    object Installed : EngineStatus()
    object Starting : EngineStatus()
    object Ready : EngineStatus()
    data class Error(val message: String) : EngineStatus()
}

sealed class InstallResult {
    object AlreadyInstalled : InstallResult()
    object Installed : InstallResult()
    object InstallationRequired : InstallResult() // User needs to manually install
    data class Failed(val reason: String) : InstallResult()
}

data class EngineChannel(
    val id: String,           // Unique identifier (infohash for AceStream)
    val name: String,
    val categories: List<String>,
    val languages: List<String>,
    val countries: List<String>,
    val iconUrl: String?,
    val metadata: Map<String, Any> = emptyMap()
)

data class StreamSession(
    val contentId: String,
    val playbackUrl: String,
    val statsUrl: String?,
    val stopSession: suspend () -> Unit
)

data class StreamStats(
    val peers: Int,
    val downloadSpeed: Long,  // bytes/sec
    val uploadSpeed: Long,    // bytes/sec
    val bufferPercent: Int
)
