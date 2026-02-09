# AetherTV

**Android TV app for AceStream P2P live TV streaming**

AetherTV is a fully self-contained Android TV application for discovering and streaming live TV content via AceStream P2P. It runs entirely on-device with no external servers.

## Features

### 📺 Channel Discovery
- **Auto-scraping** — Discovers channels from AceStream engine's search API
- **Category organization** — Sports, News, Movies, Entertainment, etc.
- **Filter rules** — Include/exclude by name, category, language, country

### 📋 TV Guide (EPG)
- **XMLTV support** — Sync from any XMLTV URL
- **Now/Next display** — See current and upcoming programs
- **Fuzzy matching** — Auto-matches channels to EPG sources
- **Timeline grid** — Navigate by time and channel

### ✅ Stream Verification
- **Live status** — Green/amber/red indicators show which channels work
- **Quality detection** — 1080p, 720p, 480p badges
- **Peer count** — See how many peers are streaming
- **Background verification** — Checks favorites automatically

### 🎮 TV-Optimized
- **D-pad navigation** — Full remote control support
- **Voice search** — "Hey Google, find ESPN"
- **Channel switching** — Up/Down during playback
- **Favorites** — Long-press to add/remove

### ♿ Accessibility
- **TalkBack support** — Screen reader compatible
- **High contrast mode** — Improved visibility option
- **Semantic labels** — All indicators properly described

### 🔄 Self-Updating
- **GitHub releases** — Checks for updates automatically
- **In-app install** — Download and update from Settings

## Requirements

- Android TV device (tested on Google Streamer 4K)
- AceStream Engine app (installs from within AetherTV)

## Installation

### Via ADB (Recommended)

```bash
# Enable Developer Options on your Android TV
# Settings → System → About → Build number (tap 7 times)
# Settings → System → Developer options → USB debugging

# Connect to your device
adb connect <device-ip>:5555

# Download latest APK from releases
# Install
adb install aethertv-v*.apk
```

### First Run

1. Launch AetherTV
2. App will check for AceStream Engine
3. If not installed, tap "Install" to get it from Play Store
4. Wait for initial channel scrape
5. Start watching!

## Usage

### Navigation

| Button | Action |
|--------|--------|
| D-pad | Navigate between items |
| Select | Play channel / Confirm |
| Long-press | Add to favorites / Options |
| Back | Go back / Exit player overlay |
| Up/Down (in player) | Switch channels |

### Settings

- **Streaming Engine** — Check engine status, install/launch
- **TV Guide (EPG)** — Enter XMLTV URL, sync program data
- **EPG Matching** — Auto-match channels to EPG sources
- **Channel Filters** — Add rules to filter channels
- **Stream Verification** — Manually verify all channels
- **Appearance** — Toggle high contrast mode
- **Diagnostics** — View/clear crash logs
- **Updates** — Check for new versions

## EPG Sources

AetherTV supports any XMLTV-format EPG. Popular sources:

- `https://iptv-org.github.io/epg/guides/` — Community guides by country
- `https://epg.best/` — Commercial EPG service

Enter the URL in Settings → TV Guide → Sync Now.

## Troubleshooting

### Channels not loading
1. Check AceStream Engine is running (Settings → Streaming Engine)
2. Tap "Refresh Status" to verify connection
3. If engine isn't running, tap "Launch Engine"

### Stream won't play
- Check the verification indicator (green = working)
- Low peer count (amber) may cause buffering
- Red indicator means channel is offline

### EPG not showing
1. Verify your XMLTV URL is accessible
2. Tap "Sync Now" in Settings → TV Guide
3. Run "Auto-Match" in EPG Channel Matching

### App crashes
- Check Settings → Diagnostics for crash logs
- Export logs and report issues

## Development

Source code is maintained locally. Only APK releases are published to GitHub.

### Building

```bash
# Requires JDK 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/aethertv-v*.apk
```

### Creating a Release

```bash
# Bump version
./scripts/bump-version.sh patch  # or minor/major

# Build and publish to GitHub
./scripts/release.sh
```

## Architecture

- **UI**: Jetpack Compose for TV
- **DI**: Hilt
- **Database**: Room (channels, EPG, favorites, filters)
- **HTTP**: Ktor Client
- **Player**: ExoPlayer (Media3)
- **Images**: Coil
- **Background**: WorkManager

## License

MIT License - See LICENSE file

## Credits

- AceStream engine for P2P streaming
- iptv-org community for EPG data
- Jetpack Compose for TV framework
