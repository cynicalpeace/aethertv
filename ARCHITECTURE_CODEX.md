# AetherTV - Technical Architecture Reference

> **Version:** 2.0
> **Last Updated:** February 8, 2026
> **Status:** Superseded by MASTER_PLAN.md v2.0 for overall architecture; this document retained as technical reference

---

> **NOTE**: MASTER_PLAN.md v2.0 is the single source of truth for architecture, file structure, database schema, and development phases. This document is retained only as a reference for detailed technical code patterns and API specifications. All Real Debrid content has been removed. The Python/FastAPI scraper has been replaced with a native Kotlin scraper ported from m3u_gen_acestream.

---

## 1. AceStream Engine API Reference

### Engine Health Check

```
GET http://127.0.0.1:6878/webui/api/service?method=get_version
Response: { "version": "3.1.77", "platform": "android" }
```

### Channel Search (Paginated)

```
GET http://127.0.0.1:6878/server/api?method=search&page_size=200&page=0

Response: {
  "result": {
    "results": [
      {
        "name": "ESPN",
        "infohash": "abc123def456...",
        "categories": ["sports"],
        "languages": ["eng"],
        "countries": ["us"],
        "status": 2,
        "availability": 0.95,
        "availability_updated_at": 1707350400,
        "icons": [{"url": "http://logo.png"}]
      }
    ],
    "total": 1500
  }
}
```

Paginate: increment `page` until `results.length < page_size`.

### Start Stream

```
GET http://127.0.0.1:6878/ace/getstream?infohash={hash}&format=json

Response: {
  "response": {
    "playback_url": "http://127.0.0.1:6878/ace/manifest.m3u8?id=...",
    "stat_url": "http://127.0.0.1:6878/ace/stat?sid=...",
    "command_url": "http://127.0.0.1:6878/ace/cmd?sid=...",
    "is_live": true
  }
}
```

### Stream Statistics

```
GET http://127.0.0.1:6878/ace/stat?sid={session_id}

Response: {
  "peers": 45,
  "speed_down": 2500,
  "speed_up": 500,
  "downloaded": 15000000,
  "uploaded": 3000000,
  "status": "playing"
}
```

### Stop Stream

```
GET http://127.0.0.1:6878/ace/cmd?sid={session_id}&method=stop
```

---

## 2. Kotlin Implementation Patterns

### AceStream Engine Client

```kotlin
class AceStreamEngineClient(
    private val httpClient: HttpClient,
    private val engineAddress: String = "http://127.0.0.1:6878"
) {
    suspend fun waitForConnection(timeout: Duration = 60.seconds) {
        withTimeout(timeout) {
            while (true) {
                try {
                    httpClient.get("$engineAddress/webui/api/service") {
                        parameter("method", "get_version")
                    }
                    return@withTimeout
                } catch (e: Exception) {
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
```

### Filter Pipeline (Ported from Go)

```kotlin
fun List<AceStreamChannel>.applyFilters(config: FilterConfig): List<AceStreamChannel> {
    return this
        .map { it.remapCategories(config.categoryRemapRules) }
        .map { it.assignCategoriesByName(config.nameToCategoryRules) }
        .filter { config.statusFilter.isEmpty() || it.status in config.statusFilter }
        .filter { it.availability >= config.availabilityThreshold }
        .filter { it.matchesCategories(config.categoryWhitelist, config.strict) }
        .filter { it.matchesLanguages(config.languageWhitelist) }
        .filter { it.matchesCountries(config.countryWhitelist) }
        .filter { it.matchesNameRegex(config.nameIncludePatterns) }
        .filterNot { it.matchesNameRegex(config.nameExcludePatterns) }
}

// Category remapping: regex-based category rename
fun AceStreamChannel.remapCategories(rules: List<CategoryRemapRule>): AceStreamChannel {
    val remapped = categories.map { cat ->
        rules.firstOrNull { Regex(it.sourcePattern, RegexOption.IGNORE_CASE).matches(cat) }
            ?.targetCategory ?: cat
    }
    return copy(categories = remapped)
}

// Name-based category assignment: infer category from channel name
fun AceStreamChannel.assignCategoriesByName(rules: List<NameToCategoryRule>): AceStreamChannel {
    if (categories.isNotEmpty()) return this
    val matched = rules.firstOrNull { Regex(it.namePattern, RegexOption.IGNORE_CASE).containsMatchIn(name) }
    return if (matched != null) copy(categories = listOf(matched.category)) else this
}
```

### Dead Source Detection (MPEG-TS)

```kotlin
suspend fun isStreamAlive(
    httpClient: HttpClient,
    url: String,
    timeout: Duration = 10.seconds,
    analyzeMpegTs: Boolean = false
): Boolean {
    return try {
        withTimeout(timeout) {
            httpClient.prepareGet(url).execute { response ->
                if (response.status.value >= 400) return@execute false
                val channel = response.bodyAsChannel()
                if (analyzeMpegTs) {
                    val buffer = ByteArray(188 * 10)
                    val bytesRead = channel.readAvailable(buffer)
                    if (bytesRead < 188) return@execute false
                    (0 until bytesRead / 188).all { i -> buffer[i * 188] == 0x47.toByte() }
                } else {
                    channel.readByte()
                    true
                }
            }
        }
    } catch (e: Exception) { false }
}
```

### EPG Fuzzy Matcher

```kotlin
class EpgMatcher {
    fun findBestMatch(aceChannel: AceStreamChannel, epgChannels: List<EpgChannel>): EpgChannel? {
        // Tier 1: Exact ID match
        aceChannel.epgId?.let { epgId ->
            epgChannels.find { it.xmltvId.equals(epgId, ignoreCase = true) }?.let { return it }
        }
        // Tier 2: Fuzzy name match
        val normalized = aceChannel.name.normalizeForMatching()
        return epgChannels
            .map { it to it.displayName.normalizeForMatching().tokenSimilarity(normalized) }
            .filter { it.second > 0.7 }
            .maxByOrNull { it.second }
            ?.first
    }

    private fun String.normalizeForMatching(): String {
        return this
            .lowercase()
            .replace(Regex("\\b(hd|fhd|sd|uhd|4k|1080p|720p|480p)\\b"), "")
            .replace(Regex("\\b(us|usa|uk|eu|int)\\b"), "")
            .replace(Regex("[^a-z0-9\\s]"), "")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    private fun String.tokenSimilarity(other: String): Double {
        val tokens1 = this.split(" ").toSet()
        val tokens2 = other.split(" ").toSet()
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0
        val intersection = tokens1.intersect(tokens2).size
        return intersection.toDouble() / maxOf(tokens1.size, tokens2.size)
    }
}
```

### Stream Verification with Quality Detection

```kotlin
class StreamVerifier(
    private val context: Context,
    private val engineClient: AceStreamEngineClient
) {
    private val semaphore = Semaphore(1) // Max 1 concurrent verification
    private val delayBetweenChecks = 10.seconds

    suspend fun verify(infohash: String): VerificationResult {
        return semaphore.withPermit {
            try {
                val streamInfo = engineClient.requestStream(infohash)
                val stats = engineClient.getStats(streamInfo.statUrl)

                if (stats.peers == 0) {
                    engineClient.stopStream(streamInfo.commandUrl)
                    return@withPermit VerificationResult.Dead(infohash)
                }

                val quality = detectQuality(streamInfo.playbackUrl)
                engineClient.stopStream(streamInfo.commandUrl)

                VerificationResult.Alive(
                    infohash = infohash,
                    peers = stats.peers,
                    quality = quality,
                    bitrate = stats.speedDown
                )
            } catch (e: Exception) {
                VerificationResult.Error(infohash, e.message)
            } finally {
                delay(delayBetweenChecks)
            }
        }
    }

    private suspend fun detectQuality(playbackUrl: String): StreamQuality {
        return withTimeoutOrNull(15.seconds) {
            suspendCancellableCoroutine { cont ->
                val player = ExoPlayer.Builder(context).build()
                player.addListener(object : Player.Listener {
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        val quality = when {
                            videoSize.height >= 1080 -> StreamQuality.FHD_1080P
                            videoSize.height >= 720  -> StreamQuality.HD_720P
                            videoSize.height >= 480  -> StreamQuality.SD_480P
                            else -> StreamQuality.LOW
                        }
                        player.release()
                        cont.resume(quality)
                    }
                })
                player.setMediaItem(MediaItem.fromUri(playbackUrl))
                player.prepare()
                cont.invokeOnCancellation { player.release() }
            }
        } ?: StreamQuality.UNKNOWN
    }
}

enum class StreamQuality { FHD_1080P, HD_720P, SD_480P, LOW, UNKNOWN }

sealed class VerificationResult {
    data class Alive(val infohash: String, val peers: Int, val quality: StreamQuality, val bitrate: Long) : VerificationResult()
    data class Dead(val infohash: String) : VerificationResult()
    data class Error(val infohash: String, val message: String?) : VerificationResult()
}
```

### ExoPlayer Configuration for Live P2P Streams

```kotlin
@Provides @Singleton
fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
    return ExoPlayer.Builder(context)
        .setLoadControl(
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(5000, 30000, 1500, 3000)
                .build()
        )
        .setTrackSelector(
            DefaultTrackSelector(context).apply {
                setParameters(
                    buildUponParameters()
                        .setPreferredVideoMimeType(MimeTypes.VIDEO_H264)
                        .setMaxVideoSize(1920, 1080)
                )
            }
        )
        .build()
}
```

---

## 3. Network Security

```xml
<!-- network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">127.0.0.1</domain>
        <domain includeSubdomains="false">localhost</domain>
    </domain-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

---

## 4. Background Processing

| Task | Mechanism | Constraint |
|------|-----------|------------|
| Scheduled scrape | WorkManager `PeriodicWorkRequest` | Network required, not during playback |
| Scheduled EPG refresh | WorkManager `PeriodicWorkRequest` | Network required, not during playback |
| User-triggered scrape | WorkManager `OneTimeWorkRequest` with foreground info | Show progress notification |
| User-triggered verification | WorkManager `OneTimeWorkRequest` with foreground info | 1 concurrent, 10s delay |
| Scheduled verification | WorkManager chained after scrape | Low priority, favorites only by default |

**Critical rule**: Use WorkManager for ALL background tasks. Do NOT use raw foreground services (deprecated `dataSync` type in Android 15). WorkManager handles OS constraints, survives app kills, and provides progress callbacks.

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.0 | 2026-02-08 | Removed Python scraper (replaced with Kotlin port), removed Real Debrid (Section 5 deleted), added AceStream search API reference, added Kotlin code patterns for scraper/EPG/verification, updated to reference MASTER_PLAN.md v2.0 as source of truth |
| 1.0 | 2026-02-08 | Initial architecture document |
