# AetherTV - Master Development Plan

> **Version:** 2.0
> **Last Updated:** February 8, 2026
> **Target Device:** Google Streamer 4K (Android TV 14, 4GB RAM, 32GB storage)
> **Status:** Architecture Review Complete → Ready for Development
> **Distribution:** Sideloaded via ADB

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Critical Design Decisions](#2-critical-design-decisions)
3. [System Architecture](#3-system-architecture)
4. [Scraper: Go-to-Kotlin Port](#4-scraper-go-to-kotlin-port)
5. [EPG Integration](#5-epg-integration)
6. [Stream Verification](#6-stream-verification)
7. [Screen Architecture](#7-screen-architecture)
8. [Development Phases](#8-development-phases)
9. [Risk Register](#9-risk-register)
10. [Testing Strategy](#10-testing-strategy)
11. [Distribution & Updates](#11-distribution--updates)
12. [Getting Started Guide](#12-getting-started-guide)

---

## 1. Executive Summary

### Project Vision

**AetherTV** is a fully self-contained Android TV application for discovering and streaming live TV content via AceStream P2P. It runs entirely on-device with no external servers or companion services. The app includes a native Kotlin scraper (ported from [m3u_gen_acestream](https://github.com/SCP002/m3u_gen_acestream)), integrated EPG, and background stream verification with quality detection.

### What Makes This Worth Building

After team analysis (including a devil's advocate review), the two genuinely novel features are:

1. **Integrated auto-scraping** -- no other Android TV app automatically discovers AceStream channels from the engine's search API with filtering, deduplication, and category mapping built in.
2. **Stream verification with quality detection** -- real-time health indicators showing which channels are actually working and at what quality.

Everything else (UI, EPG, favorites, search) exists in mature apps like TiviMate and OTT Navigator. The scraper and verification are the core value proposition.

### Target Device Specs (Google Streamer 4K)

| Component | Specification | Implication |
|-----------|---------------|-------------|
| **SoC** | MediaTek MT8696, Quad Cortex-A55 @2GHz | Efficiency cores only -- avoid parallel heavy tasks |
| **RAM** | **4 GB** | Ample headroom; ~2.2-2.6 GB free during playback |
| **Storage** | 32 GB (~24 GB user-available) | App + cache + EPG will use <400 MB |
| **OS** | Android TV 14 (API 34) | First device shipping with 14; good baseline |
| **Video Decode** | Hardware AV1, VP9, H.264, H.265 | ExoPlayer offloads to hardware |
| **Thermals** | Box form factor, excellent cooling | No throttling concerns |
| **Remote** | D-pad, Select, Back, Home, Google Assistant, Vol | **No number pad** -- channel numbers are useless |
| **Network** | Wi-Fi 5, BT 5.1, 100Mbps Ethernet | P2P streaming adequate |

### RAM Budget

| Component | Estimated RAM | Notes |
|-----------|---------------|-------|
| Android OS + System | ~1.0-1.2 GB | Fixed overhead |
| AceStream Engine (active) | ~150-250 MB | P2P engine during streaming |
| AetherTV app (UI + DB) | ~80-120 MB | Compose + Room + image cache |
| ExoPlayer (active) | ~50-100 MB | Video decode buffers |
| Background workers | ~10-30 MB | Scraper/EPG/verification |
| **Total during playback** | **~1.4-1.7 GB** | |
| **Remaining headroom** | **~2.3-2.6 GB** | Comfortable margin |

**Hard rule**: Never run scraper + EPG parser + stream verification + video playback simultaneously. Schedule heavy background work for when the user is NOT actively watching.

---

## 2. Critical Design Decisions

These decisions override anything in prior planning documents.

| Decision | Resolution | Rationale |
|----------|------------|-----------|
| **Scraper** | Native Kotlin, ported from m3u_gen_acestream Go tool | Fully self-contained, no Python/FastAPI dependency |
| **Real Debrid** | **Removed entirely** | Not relevant to AceStream P2P; eliminates complexity |
| **EPG** | **Required for MVP** | Core feature, not optional/Phase 3 |
| **Stream verification** | Background verification with quality detection | Key differentiator; shows working/dead channels |
| **Channel numbers** | **Removed entirely** | Google Streamer remote has no number pad; names > numbers |
| **Distribution** | Sideloaded via ADB | No Play Store; self-update via GitHub Releases |
| **Architecture** | Pragmatic clean arch -- no unnecessary abstraction | Devil's advocate flagged over-engineering; keep it lean |
| **Leanback vs Compose** | Compose for TV only | Cannot mix Leanback + Compose; docs were contradictory |
| **Developer verification** | Register with Google Android Developer Console by Q3 2026 | Google's 2026-2027 sideloading restrictions will block unverified APKs |

### What Was Cut from Original Scope

| Cut Feature | Reason |
|-------------|--------|
| Real Debrid integration | User decision; not relevant to P2P |
| Cloud sync / multiple profiles | Premature; no user base yet |
| Recording/DVR | Too complex for MVP; AceStream is live-only |
| Picture-in-Picture | Low priority for TV |
| Custom scraper plugins | Over-engineering |
| Donation/premium tiers | Premature |
| Trakt.tv integration | Irrelevant for live TV |
| External player option (VLC/MX) | Adds complexity; built-in player is adequate |
| Multiple themes / accent colors | Dark theme only for MVP |
| Continue Watching row | Limited value for live TV; defer to Phase 2 |
| Parental controls | Defer to Phase 2 |
| Multi-language interface | English only for MVP |
| Sports subcategories (Football > Premier League) | Use M3U group-title as-is; don't over-categorize |

---

## 3. System Architecture

### Architecture Overview

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         AETHERTV (Single APK)                            │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │                    PRESENTATION (Compose for TV)                    │  │
│  │  Home Screen │ TV Guide (EPG) │ Player │ Search │ Settings         │  │
│  └──────────────────────────┬─────────────────────────────────────────┘  │
│                              │                                           │
│  ┌──────────────────────────┴─────────────────────────────────────────┐  │
│  │                       DOMAIN (UseCases)                             │  │
│  │  GetChannels │ RefreshChannels │ GetEpg │ VerifyStreams │ Search    │  │
│  └──────────────────────────┬─────────────────────────────────────────┘  │
│                              │                                           │
│  ┌──────────────────────────┴─────────────────────────────────────────┐  │
│  │                         DATA LAYER                                  │  │
│  │  ┌─────────────┐ ┌──────────────┐ ┌───────────┐ ┌──────────────┐  │  │
│  │  │ Room DB     │ │ DataStore    │ │ Ktor HTTP │ │ ExoPlayer    │  │  │
│  │  │ (channels,  │ │ (settings)   │ │ (engine   │ │ (playback)   │  │  │
│  │  │  epg, favs) │ │              │ │  API, EPG)│ │              │  │  │
│  │  └─────────────┘ └──────────────┘ └───────────┘ └──────────────┘  │  │
│  │                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────┐   │  │
│  │  │              SCRAPER MODULE (Ported from Go)                 │   │  │
│  │  │  AceStreamEngineClient │ ChannelFilter │ StreamChecker      │   │  │
│  │  └─────────────────────────────────────────────────────────────┘   │  │
│  │                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────┐   │  │
│  │  │              EPG MODULE                                      │   │  │
│  │  │  XmltvParser │ EpgMatcher │ EpgRefreshWorker                │   │  │
│  │  └─────────────────────────────────────────────────────────────┘   │  │
│  │                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────┐   │  │
│  │  │              VERIFICATION MODULE                             │   │  │
│  │  │  StreamVerifier │ VerificationScheduler │ QualityDetector   │   │  │
│  │  └─────────────────────────────────────────────────────────────┘   │  │
│  └────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                    ACESTREAM ENGINE (Separate APK)                        │
│  P2P Engine │ HTTP API @ 127.0.0.1:6878 │ Search API │ Stream API       │
└──────────────────────────────────────────────────────────────────────────┘
```

### Tech Stack

| Layer | Technology | Notes |
|-------|------------|-------|
| UI | Jetpack Compose for TV | NOT Leanback (incompatible) |
| Language | Kotlin 2.0+ | Coroutines throughout |
| DI | Hilt | Keep modules minimal |
| Navigation | Compose Navigation | Type-safe routes |
| Database | Room | Channels, EPG, favorites, filter rules |
| Preferences | DataStore Proto | User-facing settings |
| HTTP | Ktor Client | Engine API, EPG fetching |
| Player | ExoPlayer (Media3) | Hardware decode, live stream config |
| Images | Coil | Kotlin-first, Compose integration |
| Background | WorkManager | Scheduled scrape/EPG/verification |
| Crash Reporting | Firebase Crashlytics (or Sentry) | Essential for sideloaded app |

### Database Schema

```kotlin
@Database(
    entities = [
        ChannelEntity::class,
        FavoriteEntity::class,
        WatchHistoryEntity::class,
        EpgProgramEntity::class,
        EpgChannelEntity::class,
        FilterRuleEntity::class,
        CategoryRemapRuleEntity::class
    ],
    version = 1
)
abstract class AetherTvDatabase : RoomDatabase()

// Core channel entity with verification fields
@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val infohash: String,
    val name: String,
    val categories: String,       // JSON array
    val languages: String,        // JSON array
    val countries: String,        // JSON array
    val iconUrl: String?,
    val status: Int,
    val availability: Float,
    val lastScrapedAt: Long,
    // Verification fields
    val isVerified: Boolean? = null,
    val verifiedQuality: String? = null,  // "1080p", "720p", "480p"
    val lastVerifiedAt: Long? = null,
    val verifiedPeerCount: Int? = null,
    // EPG linkage
    val epgChannelId: String? = null      // Matched XMLTV channel ID
)

// EPG program data
@Entity(tableName = "epg_programs",
    indices = [Index("channelId", "startTime")])
data class EpgProgramEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val channelId: String,        // XMLTV channel ID
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val category: String?,
    val iconUrl: String?
)
```

### File Structure

```
app/src/main/kotlin/com/aethertv/
├── AetherTvApp.kt                  # Application class
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── PlayerModule.kt
├── data/
│   ├── local/
│   │   ├── AetherTvDatabase.kt
│   │   ├── ChannelDao.kt
│   │   ├── EpgDao.kt
│   │   ├── FavoriteDao.kt
│   │   └── WatchHistoryDao.kt
│   ├── remote/
│   │   └── AceStreamEngineClient.kt   # Engine HTTP API
│   ├── repository/
│   │   ├── ChannelRepository.kt        # Interface + Impl
│   │   └── EpgRepository.kt           # Interface + Impl
│   └── preferences/
│       └── SettingsDataStore.kt
├── scraper/                             # Ported from m3u_gen_acestream
│   ├── AceStreamScraper.kt             # Orchestrator
│   ├── ChannelFilter.kt                # Filter pipeline
│   ├── StreamChecker.kt                # Dead source detection
│   ├── CategoryMapper.kt               # Category remapping
│   └── ScraperWorker.kt                # WorkManager worker
├── epg/
│   ├── XmltvParser.kt                  # Streaming SAX parser
│   ├── EpgMatcher.kt                   # Channel-to-EPG matching
│   └── EpgRefreshWorker.kt             # WorkManager worker
├── verification/
│   ├── StreamVerifier.kt               # Single-stream verification
│   ├── VerificationScheduler.kt        # Batch + rate limiting
│   └── QualityDetector.kt              # ExoPlayer-based resolution detection
├── domain/
│   ├── model/
│   │   ├── Channel.kt
│   │   ├── Category.kt
│   │   ├── EpgProgram.kt
│   │   └── StreamQuality.kt
│   └── usecase/
│       ├── GetChannelsUseCase.kt
│       ├── RefreshChannelsUseCase.kt
│       ├── SearchChannelsUseCase.kt
│       ├── GetEpgUseCase.kt
│       └── VerifyStreamsUseCase.kt
├── ui/
│   ├── theme/
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   └── Typography.kt
│   ├── components/
│   │   ├── ChannelCard.kt              # With verification indicator
│   │   ├── ChannelRow.kt
│   │   ├── VerificationDot.kt          # Green/amber/red dot
│   │   ├── QualityBadge.kt             # "1080p" badge
│   │   └── LoadingIndicator.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── guide/
│   │   ├── GuideScreen.kt              # EPG grid
│   │   └── GuideViewModel.kt
│   ├── player/
│   │   ├── PlayerScreen.kt
│   │   ├── PlayerViewModel.kt
│   │   └── MiniChannelList.kt          # Long-press overlay
│   ├── search/
│   │   ├── SearchScreen.kt
│   │   └── SearchViewModel.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt
│   │   └── SettingsViewModel.kt
│   └── navigation/
│       └── AppNavigation.kt
└── player/
    ├── AceStreamPlayer.kt
    └── MediaSessionHandler.kt
```

**~45 files total** -- lean but complete.

---

## 4. Scraper: Go-to-Kotlin Port

### Source Analysis

The Go tool [m3u_gen_acestream](https://github.com/SCP002/m3u_gen_acestream) is ~1500 lines across 7 files. Porting effort is **2-3 days** for an experienced Kotlin developer.

### What Gets Ported

| Go Module | Kotlin Equivalent | Complexity |
|-----------|-------------------|------------|
| `engine.go` (AceStream search API) | `AceStreamEngineClient.kt` | Low -- HTTP GET + JSON parse |
| `generator.go` (filter pipeline) | `ChannelFilter.kt` | Medium -- 10 sequential filters |
| `checker.go` (dead source detection) | `StreamChecker.kt` | Low -- HTTP probe + TS sync byte check |
| `config.go` (YAML config) | DataStore + Room `FilterRuleEntity` | Medium -- different paradigm |
| CLI/updater/logger | Not needed | N/A |

### AceStream Engine Search API

The scraper's core data source is the **AceStream engine's built-in search API** (not external M3U URLs):

```
GET http://127.0.0.1:6878/server/api?method=search&page_size=200&page=0

Response: {
  "result": {
    "results": [
      {
        "name": "ESPN",
        "infohash": "abc123...",
        "categories": ["sports"],
        "languages": ["eng"],
        "countries": ["us"],
        "status": 2,
        "availability": 0.95,
        "icons": [{"url": "http://..."}]
      }, ...
    ],
    "total": 1500
  }
}
```

This is paginated -- iterate until `results.size < page_size`. The entire channel catalog (~500-2000 channels) comes from the engine itself. No external M3U files needed for the primary source.

### Filter Pipeline (10 Steps)

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
        // Dead source removal is done separately (async, rate-limited)
}
```

### Dead Source Detection (MPEG-TS)

```kotlin
suspend fun isStreamAlive(url: String, analyzeMpegTs: Boolean = false): Boolean {
    return try {
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
    } catch (e: Exception) { false }
}
```

### Configuration Storage (Replacing YAML)

| Setting Type | Storage | UI |
|-------------|---------|-----|
| Simple toggles (auto-refresh, interval) | DataStore Proto | Settings screen |
| Filter rules (name regex, category lists) | Room `FilterRuleEntity` | Settings > Filter Rules |
| Category remap rules | Room `CategoryRemapRuleEntity` | Settings > Category Mapping |
| Default config | Bundled JSON asset, loaded on first run | N/A |

---

## 5. EPG Integration

### Data Source

**Primary**: [iptv-org/epg](https://github.com/iptv-org/epg) -- XMLTV format, updated daily, per-country guide files at `https://iptv-org.github.io/epg/guides/`.

**File sizes**: 1-10 MB compressed per country. User selects 1-5 relevant countries in settings.

### XMLTV Parsing (Streaming)

Critical: XMLTV files can be 50-200 MB uncompressed. **Must use streaming SAX parser, never DOM.**

```kotlin
class XmltvParser {
    fun parse(
        inputStream: InputStream,
        onChannel: (EpgChannelEntity) -> Unit,
        onProgram: (EpgProgramEntity) -> Unit
    ) {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, null)
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            when {
                parser.isStartTag("channel") -> onChannel(parseChannel(parser))
                parser.isStartTag("programme") -> onProgram(parseProgramme(parser))
            }
            parser.next()
        }
    }
}
```

### Channel-to-EPG Matching

This is the hardest EPG problem. AceStream channel names are wildly inconsistent:
```
ESPN, ESPN US, ESPN HD, ESPN FHD, ESPN (US), espn, US: ESPN, [US] ESPN
```

**Three-tier matching strategy:**
1. **Exact `tvg-id` match** -- if M3U metadata includes EPG ID, match directly
2. **Fuzzy name match** -- normalize names (lowercase, strip "HD/FHD/US/UK", remove punctuation), use token similarity, threshold >70%
3. **Manual user override** -- UI to link AceStream channel to EPG channel

**Realistic expectation: 40-60% auto-match rate.** The UI must handle channels with no EPG gracefully.

### EPG Refresh Schedule

- **Full refresh**: Daily via WorkManager at user-configured time (default 3 AM)
- **Storage**: 7-day rolling window in Room, ~33 MB for 500 channels
- **Constraint**: Only when on network, never during playback

### EPG Grid: Handling Missing Data

Channels without EPG data show a single spanning cell: "No program information available - Tap to watch". They remain fully playable. A filter option hides these channels from the grid if the user wants a cleaner view.

---

## 6. Stream Verification

### How It Works

1. Request stream from engine: `GET /ace/getstream?infohash={hash}&format=json`
2. Check stats endpoint for peer count
3. Optionally: detect quality via headless ExoPlayer `onVideoSizeChanged` callback
4. Stop the stream: `GET {command_url}?method=stop`

### Rate Limiting (Critical)

- **Max 1 concurrent verification** -- AceStream engine cannot handle multiple simultaneous streams well
- **10-second delay between checks** -- allow engine to cleanly close previous session
- **NEVER verify during active playback** -- bandwidth contention will degrade the user's stream
- **100 channels = ~17 minutes** to verify sequentially

### Quality Detection

```kotlin
// Use headless ExoPlayer to detect actual stream resolution
private suspend fun detectQuality(playbackUrl: String): StreamQuality {
    return suspendCancellableCoroutine { cont ->
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
}
```

### What Users Control (Settings > Stream Checking)

| Setting | Options | Default |
|---------|---------|---------|
| Auto-check | ON / OFF | ON |
| What to check | Favorites only / Favorites + recent / All | Favorites only |
| Frequency | 15min / 30min / 1hr / 6hr | 30 minutes |
| Notify when streams go down | ON / OFF | ON |
| Check on app launch | ON / OFF | ON |
| [Check Now] button | Manual trigger | -- |

### UI Indicators

| State | Indicator | Location |
|-------|-----------|----------|
| Verified working | Small green dot (12dp) | Channel card, EPG channel column |
| Degraded (low peers) | Small amber dot | Channel card, EPG channel column |
| Verified FAILED | Small red dot | Channel card, EPG channel column |
| Not checked | No dot | Channel card |
| Quality detected | Badge text: `[1080p]`, `[720p]` | Channel card (right side), player overlay |
| On focus | Expanded: "Verified 2 min ago, 45 peers" | Focused channel card |

---

## 7. Screen Architecture

### Home Screen (Netflix-style rows)

```
┌─────────────────────────────────────────────────────────────────┐
│ AetherTV                                       🔍 Search  ⚙    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  FAVORITES                                        See All ▶     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ ESPN     │ │ BBC One  │ │ Sky Sp   │ │ CNN      │   ───▶    │
│  │ (g)1080p │ │ (g) 720p │ │ (r) --   │ │ (g) 720p│           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│                                                                 │
│  SPORTS                                           See All ▶     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ ESPN     │ │ ESPN2    │ │ FS1      │ │ beIN     │   ───▶    │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│                                                                 │
│  NEWS                                             See All ▶     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │ CNN      │ │ BBC News │ │ Fox News │ │ MSNBC    │   ───▶    │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  🏠 Home   📺 Guide   ⭐ Favorites   🔍 Search                 │
└─────────────────────────────────────────────────────────────────┘

(g) = green verification dot    (r) = red dot (stream down)
```

**Row ordering**: Favorites first, then category rows from M3U `group-title`.

### Player Screen (No Channel Numbers)

```
┌─────────────────────────────────────────────────────────────────┐
│                         [VIDEO CONTENT]                         │
├─────────────────────────────────────────────────────────────────┤
│  ESPN                              ▶ LIVE    HD 1080p   ⭐      │
│  NFL Sunday: Patriots vs Chiefs                                 │
│  ████████████████░░░░░░░░░░░  Buffer: 98%                       │
│                                                                 │
│  ▲ BBC One                         (next in list)               │
│  ▼ Fox Sports                      (prev in list)               │
├─────────────────────────────────────────────────────────────────┤
│  [⏸/▶]              [🔊 Vol]  [⚙ Settings]  [📺 Guide]        │
└─────────────────────────────────────────────────────────────────┘
```

**Channel switching**: D-pad UP/DOWN previews adjacent channels with 2s auto-switch. Long-press UP/DOWN opens mini channel list overlay (favorites first, then alphabetical). Voice: "Hey Google, switch to ESPN."

### TV Guide (EPG Grid)

```
┌─────────────────────────────────────────────────────────────────┐
│ TV Guide                   Feb 8, 2026     Filter ▼             │
├────────┬────────────────────────────────────────────────────────┤
│        │ 12:00      12:30      13:00      13:30      14:00     │
├────────┼────────────────────────────────────────────────────────┤
│(g)ESPN │  NFL Live  │ SportsCenter │    College Football       │
│   ⭐   │   ▶ LIVE   │              │                           │
├────────┼────────────────────────────────────────────────────────┤
│(g)BBC  │      Doctor Who       │   News   │   EastEnders      │
│  One   │                       │          │                    │
├────────┼────────────────────────────────────────────────────────┤
│(r)DAZN │          No program information available              │
│        │          Tap to watch                                  │
├────────┼────────────────────────────────────────────────────────┤
│  [All] [Sports] [Movies] [News] [Entertainment] [Favorites]    │
└─────────────────────────────────────────────────────────────────┘

(g) = verified working   (r) = verified down
```

### Settings Screen

```
┌──────────────────┐  ┌──────────────────────────────────────┐
│                  │  │                                      │
│  📡 Scraper      │  │  (selected category content)         │
│  ▶ Playback      │  │                                      │
│  🔍 Stream Check │  │                                      │
│  🎨 Appearance   │  │                                      │
│  🔗 Integrations │  │                                      │
│  💾 Data         │  │                                      │
│  ℹ️ About        │  │                                      │
│                  │  │                                      │
└──────────────────┘  └──────────────────────────────────────┘
```

**Settings categories:**
- **Scraper**: Auto-refresh schedule, filter rules, category mapping, [Refresh Now]
- **Playback**: Buffer size, preferred quality, hardware acceleration
- **Stream Checking**: Auto-check toggle, scope, frequency, [Check Now]
- **Appearance**: Dark theme (MVP only), text size, animation speed, high contrast
- **Integrations**: AceStream Engine status/restart
- **Data**: Clear cache, export/import favorites, EPG country selection
- **About**: Version, update check, licenses, crash log export

---

## 8. Development Phases

### Phase 0: Scraper Port (2 weeks)

**Goal**: Port m3u_gen_acestream to Kotlin as a standalone module, tested independently.

| Task | Deliverable |
|------|-------------|
| Port `engine.go` | `AceStreamEngineClient.kt` with search pagination |
| Port `generator.go` filter pipeline | `ChannelFilter.kt` with all 10 filter steps |
| Port `checker.go` | `StreamChecker.kt` with MPEG-TS validation |
| Implement config storage | Room entities for filter rules + DataStore for simple settings |
| Unit tests | Test filters against known input/output pairs |

**Exit criteria**: Scraper module can fetch channels from a running AceStream engine, apply filters, and return a clean channel list. All tested without UI.

### Phase 1: MVP App (4 weeks)

**Goal**: Working app that can browse, play, and show EPG data for AceStream channels.

| Week | Tasks |
|------|-------|
| **1** | Project scaffolding: Gradle, Hilt DI, Compose theme, navigation skeleton, Room DB |
| **2** | Home screen with category rows, ChannelCard with D-pad navigation, favorites |
| **3** | AceStream playback integration: engine API → ExoPlayer, player screen with controls |
| **4** | EPG: XMLTV parser, Room storage, EpgMatcher, basic TV Guide grid |

**MVP features:**
- Home screen with category rows (from scraper data)
- D-pad navigation throughout
- AceStream playback via ExoPlayer
- Favorites (long-press to add/remove)
- Basic EPG grid with time navigation
- Settings: scraper refresh, EPG country selection, AceStream engine status
- First-run: engine check → initial scrape → home screen

**MVP exit criteria:**
- Browse channels by category
- Play any channel via AceStream engine
- See EPG data for matched channels
- Favorites persist across restarts
- Runs on Google Streamer 4K without crashes or OOM

### Phase 2: Verification + Polish (4 weeks)

| Week | Tasks |
|------|-------|
| **5** | Stream verification: verifier, scheduler, rate limiter, UI indicators |
| **6** | Search (keyboard + voice), verification settings UI |
| **7** | Watch history, manual EPG channel matching, filter rules settings UI |
| **8** | Performance profiling on device, memory optimization, focus management polish |

**Phase 2 features:**
- Stream verification with quality detection badges
- Verification scheduling (auto-check favorites)
- Search with voice support
- Watch history
- Manual EPG channel override
- Scraper filter configuration UI
- Mini channel list overlay (long-press during playback)

### Phase 3: Release (2 weeks)

| Week | Tasks |
|------|-------|
| **9** | Accessibility audit, error state polish, crash reporting integration |
| **10** | CI/CD pipeline, signed release build, self-update mechanism, documentation |

**Phase 3 features:**
- Self-update via GitHub Releases
- Firebase Crashlytics integration
- TalkBack support
- High contrast mode
- Full error state handling
- Signed release APK

### Timeline Visualization

```
Week:  1    2    3    4    5    6    7    8    9   10   11   12
       ├────┴────┤────┴────┴────┴────┤────┴────┴────┴────┤────┴────┤
       │ PH 0   │    PHASE 1 MVP    │  PHASE 2 POLISH   │ PH 3   │
       │ Scraper│                    │                    │Release │
       │ Port   │ • Scaffolding     │ • Verification     │ • A11y │
       │        │ • Home screen     │ • Search           │ • CI/CD│
       │ • Port │ • Player          │ • Watch history    │ • Self │
       │ • Test │ • EPG             │ • Filter rules UI  │  update│
       │        │ • Favorites       │ • Perf profiling   │ • Sign │
```

**Total: ~12 weeks** -- aggressive but feasible because:
- Scraper port is small (~1500 LOC, 2-3 days core work + 1 week testing)
- Architecture is pragmatic (no over-abstraction)
- Features cut from original scope reduce 30-40% of work
- Single target device simplifies testing

---

## 9. Risk Register

### Red Flags (Must Address)

| Risk | Impact | Mitigation |
|------|--------|------------|
| **Google 2026-2027 sideloading restrictions** | App blocked on certified devices | Register as verified developer through Android Developer Console NOW; register package name + signing key |
| **CPU saturation during concurrent tasks** | ANRs, dropped frames, OOM | Never run scraper + EPG + verification + playback simultaneously; use WorkManager with mutual exclusion |
| **`dataSync` foreground service deprecated in Android 15** | Breaking change when device updates | Use WorkManager instead of foreground services for all scheduled tasks from day 1 |

### Yellow Flags (Proceed with Caution)

| Risk | Impact | Mitigation |
|------|--------|------------|
| AceStream engine instability | Crashes, unresponsive API | Robust error handling, auto-retry with backoff, clear UI guidance |
| EPG matching quality (~40-60% auto) | Half-empty EPG grid | Show "No EPG data" gracefully, support manual matching, filter option to hide unmatched |
| Stream verification staleness | "Verified" channels go dead minutes later | Show "Verified X min ago", re-verify favorites on shorter cycle, warn users verification is a snapshot |
| AceStream channel ID rotation | Favorites/history break when IDs change | Match on name + category as fallback, notify user, offer to update |
| Scraper source changes | Channels stop appearing | Engine search API is self-contained (unlike external URLs), reducing this risk significantly |

### Green Flags (Advantages)

| Factor | Benefit |
|--------|---------|
| 4 GB RAM on Google Streamer 4K | ~2.4 GB headroom during playback; no OOM concerns |
| Box form factor | Zero thermal throttling |
| Android TV doesn't Doze (always plugged in) | Background services run reliably |
| AceStream engine search API | Channel data comes from engine itself; no fragile external scraping |
| Small Go codebase (~1500 LOC) | Port is manageable, not a rewrite |

---

## 10. Testing Strategy

### Testing Pyramid

```
        ┌───────────────┐
        │   E2E/Device  │  ~10% (manual on Google Streamer 4K)
        ├───────────────┤
        │  Integration  │  ~25% (Room, Ktor, WorkManager)
        ├───────────────┤
        │  Unit Tests   │  ~65% (Scraper, Filters, ViewModels)
        └───────────────┘
```

### Key Test Areas

| Area | Type | Priority |
|------|------|----------|
| Scraper filter pipeline | Unit | P0 -- must match Go tool output |
| XMLTV parser | Unit | P0 -- must handle malformed input |
| EPG matcher (fuzzy) | Unit | P0 -- test normalization + scoring |
| ChannelDao queries | Integration | P1 |
| AceStreamEngineClient | Integration (mock server) | P1 |
| Stream verification flow | Integration | P1 |
| Home screen rendering | Compose UI test | P2 |
| D-pad focus management | Manual device test | P2 |
| Memory under load | Profiling on device | P1 |

### Device Testing

**Primary target only**: Google Streamer 4K. Do not spread testing effort across Shield TV, Fire TV, Mi Box for MVP. Optimize for one device, expand compatibility in future releases.

**Pre-release checklist:**
- App installs cleanly via `adb install`
- All navigation flows work with Google Streamer remote
- AceStream engine detected and streams play
- Playback starts within 10 seconds (P2P warmup)
- No ANRs during 30-minute viewing session
- Memory usage stable (<200 MB for app, excluding AceStream engine)
- Background scrape completes without OOM
- EPG loads and displays for matched channels
- Verification runs and indicators update

---

## 11. Distribution & Updates

### Sideloading Process

```bash
# Enable Developer Options on Google Streamer 4K
# Settings → System → About → Build number (tap 7 times)
# Settings → System → Developer options → USB debugging

adb connect <device-ip>:5555
adb install aethertv-release.apk
```

### Self-Update Mechanism

1. App checks GitHub Releases API at launch: `GET https://api.github.com/repos/{owner}/aethertv/releases/latest`
2. Compares `tag_name` with `BuildConfig.VERSION_NAME`
3. If newer: shows non-intrusive banner on Home screen "Update available: v1.2.0"
4. User clicks → DownloadManager fetches APK → PackageInstaller prompts install
5. Requires `REQUEST_INSTALL_PACKAGES` permission in manifest

### APK Signing

- Dedicated release signing key (NOT in repo)
- GitHub Actions signs release builds using repository secrets
- APK Signature Scheme v2+ (required for Android 14)
- Register signing key fingerprint with Google Android Developer Console for sideloading verification

### Developer Verification (Action Required by Q3 2026)

Google is rolling out sideloading restrictions globally in 2026-2027:
- Register through [Android Developer Console](https://developer.android.com/)
- Verify developer identity
- Register `com.aethertv.app` package name
- Register signing key fingerprint
- This is free and does NOT require Play Store publishing

---

## 12. Getting Started Guide

### Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Android Studio | Latest stable | IDE |
| JDK | 17+ | Bundled with Android Studio |
| Kotlin | 2.0+ | Via Gradle plugin |
| ADB | Latest | Device deployment |
| Google Streamer 4K | Android TV 14 | Target device |
| AceStream Engine APK | 3.1.77+ (arm64) | Streaming engine |

### Quick Start

```bash
# 1. Clone and open in Android Studio
git clone https://github.com/youruser/aethertv.git
cd aethertv

# 2. Let Gradle sync (downloads dependencies)

# 3. Set up Android TV emulator for UI development
#    Tools → Device Manager → Create → TV → 1080p → API 34 (google_atv)

# 4. Build debug APK
./gradlew assembleDebug

# 5. Deploy to Google Streamer 4K
adb connect <device-ip>:5555
adb install app/build/outputs/apk/debug/app-debug.apk

# 6. Ensure AceStream Engine is installed and running on device
adb install acestreamengine-3.1.77-arm64.apk
```

### Environment Configuration

`local.properties` (not committed):
```properties
sdk.dir=/path/to/android/sdk
SCRAPER_DEFAULT_SOURCES=bundled   # Use bundled default config
```

### Development Workflow

| Task | Where | Notes |
|------|-------|-------|
| UI development | Android TV emulator | Fast iteration, Live Edit |
| Scraper testing | Unit tests | Mock engine responses |
| AceStream integration | Physical device | Engine only runs on ARM |
| Performance profiling | Physical device | Emulator not representative |
| EPG testing | Either | Mock XMLTV files for unit tests |

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.0 | 2026-02-08 | Complete revision: removed Python scraper (native Kotlin port), removed Real Debrid, EPG required for MVP, added stream verification, removed channel numbers, realistic scope cuts, risk register, developer verification requirement |
| 1.0 | 2026-02-08 | Initial master plan |

---

*This document is the single source of truth for AetherTV. Updated after multi-agent architecture review including devil's advocate analysis.*
