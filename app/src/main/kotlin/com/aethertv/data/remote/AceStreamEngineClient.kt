package com.aethertv.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class AceStreamEngineClient @Inject constructor(
    private val httpClient: HttpClient,
    private val engineAddress: String = "http://127.0.0.1:6878",
) {
    companion object {
        // H38 fix: Cap maximum pages to prevent infinite loop if API always returns full pages
        private const val MAX_PAGES = 100
    }
    suspend fun waitForConnection(timeout: Duration = 60.seconds) {
        withTimeout(timeout) {
            while (true) {
                try {
                    httpClient.get("$engineAddress/webui/api/service") {
                        parameter("method", "get_version")
                    }
                    return@withTimeout
                } catch (_: Exception) {
                    delay(5.seconds)
                }
            }
        }
    }

    suspend fun searchAll(): List<AceStreamChannel> {
        val allItems = mutableListOf<AceStreamChannel>()
        var page = 0
        val pageSize = 200
        while (true) {
            val result: SearchResponse = httpClient.get("$engineAddress/server/api") {
                parameter("method", "search")
                parameter("page_size", pageSize)
                parameter("page", page)
            }.body()
            allItems.addAll(result.result.results)
            if (result.result.results.size < pageSize) break
            page++
            // H38 fix: Prevent infinite pagination
            if (page >= MAX_PAGES) break
        }
        return allItems
    }

    suspend fun requestStream(infohash: String): StreamInfo {
        return httpClient.get("$engineAddress/ace/getstream") {
            parameter("infohash", infohash)
            parameter("format", "json")
        }.body<StreamResponse>().response
    }

    suspend fun getStats(statUrl: String): StreamStats {
        return httpClient.get(statUrl).body()
    }

    suspend fun stopStream(commandUrl: String) {
        httpClient.get(commandUrl) {
            parameter("method", "stop")
        }
    }
}

// API response models

@Serializable
data class SearchResponse(
    val result: SearchResult,
)

@Serializable
data class SearchResult(
    val results: List<AceStreamChannel>,
    val total: Int,
)

@Serializable
data class AceStreamChannel(
    val name: String,
    val infohash: String,
    val categories: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val countries: List<String> = emptyList(),
    val status: Int = 0,
    val availability: Float = 0f,
    @SerialName("availability_updated_at")
    val availabilityUpdatedAt: Long = 0,
    val icons: List<ChannelIcon> = emptyList(),
) {
    val iconUrl: String? get() = icons.firstOrNull()?.url
}

@Serializable
data class ChannelIcon(
    val url: String,
)

@Serializable
data class StreamResponse(
    val response: StreamInfo,
)

@Serializable
data class StreamInfo(
    @SerialName("playback_url")
    val playbackUrl: String,
    @SerialName("stat_url")
    val statUrl: String,
    @SerialName("command_url")
    val commandUrl: String,
    @SerialName("is_live")
    val isLive: Boolean = true,
)

@Serializable
data class StreamStats(
    val peers: Int = 0,
    @SerialName("speed_down")
    val speedDown: Long = 0,
    @SerialName("speed_up")
    val speedUp: Long = 0,
    val downloaded: Long = 0,
    val uploaded: Long = 0,
    val status: String = "",
)
