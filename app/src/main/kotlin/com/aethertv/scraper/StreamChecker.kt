package com.aethertv.scraper

import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readByte
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class StreamChecker @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun isStreamAlive(
        url: String,
        timeout: Duration = 10.seconds,
        analyzeMpegTs: Boolean = false,
    ): Boolean {
        return try {
            withTimeout(timeout) {
                httpClient.prepareGet(url).execute { response ->
                    if (response.status.value >= 400) return@execute false
                    val channel = response.bodyAsChannel()
                    if (analyzeMpegTs) {
                        val buffer = ByteArray(188 * 10) // 10 TS packets
                        val bytesRead = channel.readAvailable(buffer)
                        if (bytesRead < 188) return@execute false
                        // Validate TS sync byte (0x47) every 188 bytes
                        (0 until bytesRead / 188).all { i -> buffer[i * 188] == 0x47.toByte() }
                    } else {
                        channel.readByte()
                        true
                    }
                }
            }
        } catch (_: Exception) {
            false
        }
    }
}
