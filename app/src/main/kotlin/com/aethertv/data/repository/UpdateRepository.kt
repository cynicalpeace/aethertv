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
    
    private fun isNewerVersion(latest: String, current: String): Boolean {
        // Handle debug suffix
        val latestClean = latest.removeSuffix("-debug").split(".")
        val currentClean = current.removeSuffix("-debug").split(".")
        
        for (i in 0 until maxOf(latestClean.size, currentClean.size)) {
            val l = latestClean.getOrNull(i)?.toIntOrNull() ?: 0
            val c = currentClean.getOrNull(i)?.toIntOrNull() ?: 0
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
    
    fun downloadUpdate(release: GitHubRelease): Flow<UpdateState> = flow {
        emit(UpdateState.Downloading(0f))
        
        val apkAsset = release.assets.find { it.name.endsWith(".apk") }
            ?: run {
                emit(UpdateState.Error("No APK found in release"))
                return@flow
            }
        
        try {
            val apkFile = File(context.cacheDir, APK_FILENAME)
            
            // Simple download - write full response to file
            val response: HttpResponse = httpClient.get(apkAsset.browser_download_url)
            val bytes = response.body<ByteArray>()
            
            emit(UpdateState.Downloading(0.5f))
            
            withContext(Dispatchers.IO) {
                apkFile.writeBytes(bytes)
            }
            
            emit(UpdateState.Downloading(1f))
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
