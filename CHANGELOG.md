# Changelog

All notable changes to AetherTV will be documented in this file.

## [2.0.2] - 2026-02-09

### Fixed
- Added error handling in HomeViewModel flow collection
- Included all v2.0.0 audit fixes that were not in previous releases
- Added crash logging for better debugging
- Added ScraperState for progress tracking
- Various thread-safety and lifecycle fixes

### Added
- CrashLogger utility for capturing crash logs
- High contrast accessibility mode
- Improved EPG matching with caching

## [2.0.1] - 2026-02-09

### Fixed
- **Critical**: Fixed crash on app update due to database schema mismatch
- Added `fallbackToDestructiveMigration` to handle schema changes gracefully
- Database will recreate tables on schema change (channels will re-scrape on first launch)

## [2.0.0] - 2026-02-08

### Fixed (7 Audit Rounds - 20 Critical + 38 High Priority Bugs)
- **C18**: EPG refresh now correctly preserves data from all configured countries (was wiping previous country data)
- **C19**: Fixed thread-safety issue with time formatter in Now/Next indicators
- **C20**: Removed main-thread blocking when leaving player screen
- **H38**: Added pagination limit to prevent infinite API loops
- **H40**: Fixed crash when scraper logs have same-millisecond timestamps

### Security & Stability
- 7 complete code audits by specialist agents (Security, Concurrency, Lifecycle, Compose, Devil's Advocate)
- All critical race conditions resolved with mutex/atomic patterns
- Thread-safe date formatting throughout EPG components
- Memory pressure handling in EPG parser and scraper
- Transactional database operations for data integrity

### Production Ready
- Comprehensive ProGuard rules for Media3/ExoPlayer
- Proper lifecycle cleanup for AceStream sessions
- Locale-independent string operations (Turkish locale fix)

## [1.9.0] - 2026-02-08

### Fixed
- **Button Focus**: Fixed buttons in Settings not highlighting when focused
- **Focus Feedback**: All interactive elements now show proper visual focus with border
- **Null Safety**: Fixed potential crash in accessibility description for unverified channels

### Added
- **FocusableSurface Components**: Reusable TV-optimized button components (TvButton, TvChip, TvCompactButton)
- **README**: Comprehensive documentation with installation, usage, and troubleshooting guides

### Improved
- Filter type chips now highlight properly when focused
- Toggle and delete buttons for filter rules show focus state
- Consistent focus behavior across Settings, FirstRun, and other screens

## [1.8.0] - 2026-02-08

### Added
- **TalkBack Accessibility**: Content descriptions for all channel cards, verification indicators, and quality badges
- **High Contrast Mode**: Toggle in Settings > Appearance for improved visibility
- **Error Components**: Reusable ErrorScreen and ErrorBanner for consistent error handling
- **Crash Logging**: Local crash log capture with view/clear in Settings > Diagnostics

### Accessibility
- ChannelCard now announces: name, status, quality, favorite state, and current program
- VerificationDot describes status: "Online with X peers", "Offline", "Low peers"
- QualityBadge announces resolution: "Full HD 1080p quality", etc.

## [1.7.0] - 2026-02-08

### Fixed
- **In-app updates**: Fixed version display showing incorrect value
- **APK naming**: Consistent naming pattern `aethertv-v{version}-debug.apk`
- **Update detection**: Improved APK asset detection in GitHub releases

### Added
- Release script for APK-only GitHub releases
- Version bump helper script

### Changed
- Source code no longer pushed to GitHub (APK releases only)

## [1.6.0] - 2026-02-08

### Added
- **Channel Filters**: Filter by name patterns, category, language, country
- **EPG Matching**: Auto-match channels to EPG sources
- **EPG Settings**: XMLTV URL input with sync progress
- **TV Guide**: Timeline grid with Now/Next display
- **Bundled Engine**: One-tap AceStream installation

### Changed
- Improved focus management across all screens
- Better D-pad navigation in player

## [1.5.0] - 2026-02-08

### Added
- EPG URL configuration in Settings
- Guide button on home screen
- EPG sync progress indicator

## [1.4.0] - 2026-02-08

### Added
- Full TV Guide screen with timeline grid
- Now/Next program display on channel cards
- Bundled AceStream APK (78MB, ARM64)
- Red current-time indicator in guide

## [1.3.0] - 2026-02-08

### Added
- Voice search integration
- Engine management in Settings
- Stream verification with quality detection

## [1.2.0] - 2026-02-08

### Added
- StreamEngine abstraction layer
- Swappable P2P backend support

## [1.1.0] - 2026-02-08

### Added
- Stream verification UI
- Quality badges on channels
- Verification progress in Settings

## [1.0.0] - 2026-02-08

### Added
- First-run setup flow
- Favorites system (long-press to favorite)
- Watch history with "Recently Watched"
- Buffer indicator during playback
- Channel switching with D-pad up/down
- Search screen with voice support
- Settings with in-app updates
- ExoPlayer integration for AceStream
