package com.aethertv.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val name: String,
    val body: String? = null,
    val published_at: String,
    val assets: List<GitHubAsset>
)

@Serializable
data class GitHubAsset(
    val name: String,
    val browser_download_url: String,
    val size: Long
)

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class Available(val release: GitHubRelease, val currentVersion: String) : UpdateState()
    object UpToDate : UpdateState()
    data class Downloading(val progress: Float) : UpdateState()
    data class ReadyToInstall(val apkFile: File) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

@Singleton
class UpdateRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val context: Context
) {
    companion object {
        private const val GITHUB_API = "https://api.github.com/repos/cynicalpeace/aethertv/releases/latest"
        private const val APK_FILENAME = "aethertv-update.apk"
    }
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun getCurrentVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    suspend fun checkForUpdate(): UpdateState = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = httpClient.get(GITHUB_API) {
                header("Accept", "application/vnd.github.v3+json")
            }
            
            if (response.status != HttpStatusCode.OK) {
                return@withContext UpdateState.Error("Failed to check for updates: ${response.status}")
            }
            
            val releaseJson = response.bodyAsText()
            val release = json.decodeFromString<GitHubRelease>(releaseJson)
            
            val currentVersion = getCurrentVersion()
            val latestVersion = release.tag_name.removePrefix("v")
            
            // Simple version comparison (works for our versioning scheme)
            if (isNewerVersion(latestVersion, currentVersion)) {
                UpdateState.Available(release, currentVersion)
            } else {
                UpdateState.UpToDate
            }
        } catch (e: Exception) {
            UpdateState.Error("Update check failed: ${e.message}")
        }
    }
    
    /**
     * Compare version strings to determine if latest is newer than current.
     * Handles versions like: "1.2.3", "1.2.3-debug", "1.2.3-beta1", "1.2.3-rc2"
     * Strips all non-numeric suffixes from each component (H27 fix).
     */
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestClean = parseVersionParts(latest)
        val currentClean = parseVersionParts(current)
        
        for (i in 0 until maxOf(latestClean.size, currentClean.size)) {
            val l = latestClean.getOrNull(i) ?: 0
            val c = currentClean.getOrNull(i) ?: 0
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
    
    /**
     * Parse version string into list of integer parts.
     * "1.2.3-beta1" → [1, 2, 3]
     * "1.2.3.4" → [1, 2, 3, 4]
     * 
     * Handles malformed versions gracefully (H36 fix):
     * - Very long digit sequences are capped to prevent overflow
     * - Invalid parts are treated as 0
     */
    private fun parseVersionParts(version: String): List<Int> {
        // Remove common suffixes first
        val withoutSuffix = version
            .removeSuffix("-debug")
            .removeSuffix("-release")
        
        return withoutSuffix.split(".").mapNotNull { part ->
            // Extract leading digits from each part
            // "3-beta1" → "3" → 3
            // "10rc2" → "10" → 10
            val digits = part.takeWhile { it.isDigit() }
            
            // H36 fix: Cap digit length to prevent integer overflow
            // Version numbers should never exceed 9 digits (999,999,999)
            if (digits.length > 9) {
                Int.MAX_VALUE  // Treat as "very large" version
            } else {
                digits.toIntOrNull() ?: 0
            }
        }
    }
    
    fun downloadUpdate(release: GitHubRelease): Flow<UpdateState> = flow {
        emit(UpdateState.Downloading(0f))
        
        // Look for APK with our naming convention first, fall back to any .apk
        val apkAsset = release.assets.find { it.name.startsWith("aethertv-") && it.name.endsWith(".apk") }
            ?: release.assets.find { it.name.endsWith(".apk") }
            ?: run {
                emit(UpdateState.Error("No APK found in release"))
                return@flow
            }
        
        try {
            val apkFile = File(context.cacheDir, APK_FILENAME)
            val totalSize = apkAsset.size
            
            // Track progress externally for proper emission (H23 fix)
            var lastEmittedProgress = 0f
            
            withContext(Dispatchers.IO) {
                // Stream download to file to avoid OOM on large APKs
                httpClient.prepareGet(apkAsset.browser_download_url).execute { response ->
                    if (response.status.value >= 400) {
                        throw Exception("Download failed with status: ${response.status}")
                    }
                    
                    val channel = response.bodyAsChannel()
                    
                    // Delete existing file if any
                    if (apkFile.exists()) {
                        apkFile.delete()
                    }
                    
                    apkFile.outputStream().buffered().use { output ->
                        val buffer = ByteArray(8192)
                        var bytesDownloaded = 0L
                        
                        while (!channel.isClosedForRead) {
                            val bytesRead = channel.readAvailable(buffer)
                            if (bytesRead > 0) {
                                output.write(buffer, 0, bytesRead)
                                bytesDownloaded += bytesRead
                                
                                // Track progress for emission after this block
                                lastEmittedProgress = if (totalSize > 0) {
                                    (bytesDownloaded.toFloat() / totalSize).coerceIn(0f, 1f)
                                } else {
                                    0.5f // Unknown size
                                }
                            }
                        }
                    }
                }
            }
            
            // Emit final progress state
            emit(UpdateState.Downloading(lastEmittedProgress.coerceAtLeast(0.99f)))
            emit(UpdateState.ReadyToInstall(apkFile))
        } catch (e: Exception) {
            emit(UpdateState.Error("Download failed: ${e.message}"))
        }
    }
    
    fun installApk(apkFile: File) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
        } else {
            Uri.fromFile(apkFile)
        }
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }
}
