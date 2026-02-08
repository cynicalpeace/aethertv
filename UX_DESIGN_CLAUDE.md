# AetherTV - UX/UI Design Document

> **Version:** 2.0
> **Target Platform:** Android TV (Google Streamer 4K)
> **Design Philosophy:** TV-first, remote-friendly, accessible from 10ft away

---

> **IMPORTANT v2.0 CHANGES** (see MASTER_PLAN.md v2.0 for details):
>
> - **Channel numbers REMOVED** -- Google Streamer remote has no number pad. All references to channel numbers (CH+ 124, number key input, "Show channel numbers" setting) are eliminated. Player overlay shows channel names only: "▲ BBC One" / "▼ Fox Sports"
> - **Real Debrid REMOVED** -- All Real Debrid UI elements removed from Settings > Integrations
> - **Stream verification indicators ADDED** -- Channel cards show verification status: green dot (working), amber dot (degraded), red dot (failed). Quality badges: [1080p], [720p]. Expanded detail on focus.
> - **Stream Checking settings ADDED** -- New settings category between Playback and Appearance: auto-check toggle, scope (favorites/all), frequency, [Check Now] button
> - **EPG required from day 1** -- TV Guide is MVP, not Phase 2. Channels without EPG show "No program info" spanning cell.
> - **Quick channel switch**: Long-press UP/DOWN in player opens mini channel list (favorites first, then alphabetical). Voice: "Hey Google, switch to ESPN". Number key input removed.
> - **Settings restructured**: Scraper | Playback | Stream Checking (NEW) | Appearance | Integrations (no Real Debrid) | Data | About

---

## Table of Contents

1. [User Journey Mapping](#1-user-journey-mapping)
2. [Screen-by-Screen Design](#2-screen-by-screen-design)
3. [Remote Control Navigation](#3-remote-control-navigation)
4. [Visual Design Guidelines](#4-visual-design-guidelines)
5. [Premium Features Brainstorm](#5-premium-features-brainstorm)
6. [Accessibility Considerations](#6-accessibility-considerations)
7. [Error States & Edge Cases](#7-error-states--edge-cases)
8. [Menu Structure](#8-menu-structure)
9. [Wireframes & Mockup Notes](#9-wireframes--mockup-notes)

---

## 1. User Journey Mapping

### 1.1 First Launch Experience

```
┌─────────────────────────────────────────────────────────────────────┐
│                        FIRST LAUNCH FLOW                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Welcome Screen]                                                   │
│       │                                                             │
│       ▼                                                             │
│  [AceStream Engine Check]──No──▶ [Install Prompt + Guide]           │
│       │                                  │                          │
│       │ Yes                              ▼                          │
│       ▼                          [Open Play Store]                  │
│  [Initial Scrape Prompt]                 │                          │
│  "Fetch channel list now?"               ▼                          │
│       │                          [Return after install]             │
│       ▼                                                             │
│  [Scraping Progress]                                                │
│  (Full-screen with animation)                                       │
│       │                                                             │
│       ▼                                                             │
│  [Quick Tour] (Optional, skippable)                                 │
│  - 3-4 slides explaining features                                   │
│  - Remote navigation tips                                           │
│       │                                                             │
│       ▼                                                             │
│  [Home Screen] ✓                                                    │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Key Design Decisions:**
- Welcome screen shows app logo with brief tagline (3 seconds, auto-advance)
- Engine check happens automatically with clear status indicator
- First scrape is mandatory but shows engaging progress animation
- Quick tour uses large illustrations, minimal text

### 1.2 Finding a Channel to Watch

**Primary Path (3 clicks or less):**
```
Home → Featured Row → Select Channel → Play
```

**Alternative Paths:**
```
Home → TV Guide → Browse Grid → Select → Play
Home → Categories → Sports → Football → Select → Play
Home → Search → Type Query → Select Result → Play
```

**Design Principle:** Most users should find something to watch within 10 seconds of opening the app.

### 1.3 Browsing by Category

```
┌─────────────────────────────────────────────────────────────────────┐
│                      CATEGORY BROWSING FLOW                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Categories Screen]                                                │
│       │                                                             │
│       ├──▶ [Sports]                                                 │
│       │        ├── Football (Live: 12, VOD: 45)                     │
│       │        ├── Basketball (Live: 8, VOD: 23)                    │
│       │        ├── Tennis (Live: 4, VOD: 12)                        │
│       │        └── [More Sports...]                                 │
│       │                                                             │
│       ├──▶ [Movies]                                                 │
│       │        ├── Action                                           │
│       │        ├── Comedy                                           │
│       │        └── Drama                                            │
│       │                                                             │
│       ├──▶ [News]                                                   │
│       │        ├── By Country                                       │
│       │        └── By Language                                      │
│       │                                                             │
│       ├──▶ [Entertainment]                                          │
│       │                                                             │
│       └──▶ [Kids]                                                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Category Card Shows:**
- Category icon (large, recognizable)
- Category name
- Live channel count badge
- Thumbnail preview (optional: cycles through channel logos)

### 1.4 Searching for Specific Content

```
┌─────────────────────────────────────────────────────────────────────┐
│                         SEARCH FLOW                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Search Screen Opens]                                              │
│       │                                                             │
│       ├──▶ [Recent Searches] (if any)                               │
│       │        └── One-click to repeat                              │
│       │                                                             │
│       ├──▶ [Voice Search] (if mic button pressed)                   │
│       │        └── "Search for: ESPN" confirmation                  │
│       │                                                             │
│       └──▶ [On-screen Keyboard]                                     │
│                │                                                    │
│                ▼                                                    │
│           [Live Results] (updates as you type)                      │
│                │                                                    │
│                ├── Channels matching query                          │
│                ├── Categories matching query                        │
│                └── Suggestions ("Did you mean...")                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Search Features:**
- Instant search (results update with each keystroke)
- Voice search support (Android TV native)
- Search history (last 10 searches, clearable)
- Fuzzy matching ("espn" matches "ESPN", "ESPN2", "ESPN+")

### 1.5 Managing Favorites

```
┌─────────────────────────────────────────────────────────────────────┐
│                       FAVORITES FLOW                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [Adding to Favorites]                                              │
│  ─────────────────────                                              │
│  Option A: Long-press on channel → "Add to Favorites"               │
│  Option B: During playback → Press ★ button → Toggle favorite       │
│  Option C: Channel detail screen → Favorite toggle button           │
│                                                                     │
│  [Viewing Favorites]                                                │
│  ──────────────────                                                 │
│  Main Menu → Favorites                                              │
│       │                                                             │
│       ├── Grid of favorited channels                                │
│       ├── Sort by: Recently Added / Alphabetical / Most Watched     │
│       └── Filter by: Category / Live Now                            │
│                                                                     │
│  [Managing Favorites]                                               │
│  ───────────────────                                                │
│  Long-press on favorite → Remove / Reorder                          │
│  Settings → Data → Export Favorites (JSON)                          │
│  Settings → Data → Import Favorites                                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.6 Accessing Settings

**Quick Settings (Overlay):**
- Accessible from any screen via dedicated button (Menu/⚙️)
- Shows: Audio, Quality, Subtitles (during playback)
- Shows: Theme toggle, Refresh data (from other screens)

**Full Settings:**
- Main Menu → Settings (last item)
- Grouped into logical categories (see Settings Screen design)

---

## 2. Screen-by-Screen Design

### 2.1 Home Screen

```
┌─────────────────────────────────────────────────────────────────────┐
│ ◀ AceTV                                    🔍 Search    ⚙ Settings │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    FEATURED / NOW LIVE                       │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐            │   │
│  │  │ ▶ LIVE  │ │ ▶ LIVE  │ │ ▶ LIVE  │ │ ▶ LIVE  │   ───▶    │   │
│  │  │ ESPN    │ │ BBC One │ │ Sky Sp  │ │ CNN     │            │   │
│  │  │ NFL...  │ │ Doctor..│ │ PL Live │ │ Breaking│            │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘            │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  CONTINUE WATCHING                                                  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐                               │
│  │ ▶ 45min │ │ ▶ 1:20  │ │ ▶ 30min │                               │
│  │ HBO     │ │ Fox     │ │ ABC     │                               │
│  └─────────┘ └─────────┘ └─────────┘                               │
│                                                                     │
│  SPORTS                                           See All ▶         │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                   │
│  │ ESPN    │ │ ESPN2   │ │ FS1     │ │ NBCSN   │   ───▶           │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘                   │
│                                                                     │
│  MOVIES                                           See All ▶         │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                   │
│  │ HBO     │ │ Showtime│ │ Starz   │ │ Cinemax │   ───▶           │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘                   │
│                                                                     │
│  NEWS                                             See All ▶         │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                   │
│  │ CNN     │ │ BBC News│ │ Fox News│ │ MSNBC   │   ───▶           │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘                   │
│                                                                     │
├─────────────────────────────────────────────────────────────────────┤
│  🏠 Home   📺 Guide   📁 Categories   ⭐ Favorites   🔍 Search     │
└─────────────────────────────────────────────────────────────────────┘
```

**Layout: Hybrid (Rows of Cards)**

**Featured Section:**
- Large hero cards (16:9 aspect ratio)
- Auto-advances every 5 seconds (stops on focus)
- Shows: Channel logo, current program, LIVE badge, viewer count (if available)
- Card dimensions: 320x180dp (focused: 352x198dp with elevation)

**Category Rows:**
- Horizontal scrolling rows
- Each row: Category title + "See All" link
- Cards: 160x90dp (channel logo + name + status indicator)
- Show 4-5 cards visible, overflow indicated with fade

**Continue Watching:**
- Only shows if user has watch history
- Shows timestamp of last position
- Quick resume on select

**Bottom Navigation Bar:**
- Fixed position
- 5 main destinations
- Current selection highlighted
- D-pad down from content focuses nav bar

### 2.2 TV Guide / Channel Browser

```
┌─────────────────────────────────────────────────────────────────────┐
│ ◀ TV Guide                    Feb 8, 2026    🔍    Filter ▼    ⚙  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────┬─────────────────────────────────────────────────────────┐│
│  │      │ 12:00      12:30      13:00      13:30      14:00      ││
│  ├──────┼─────────────────────────────────────────────────────────┤│
│  │      │            │          │                    │           ││
│  │ ESPN │  NFL Live  │ SportsCe │    College Football           ││
│  │  ⭐  │   ▶ LIVE   │          │                    │           ││
│  ├──────┼─────────────────────────────────────────────────────────┤│
│  │      │                       │          │                     ││
│  │ BBC  │      Doctor Who       │   News   │   EastEnders       ││
│  │ One  │                       │          │                     ││
│  ├──────┼─────────────────────────────────────────────────────────┤│
│  │      │          │            │                    │           ││
│  │ HBO  │  Movie:  │  Last Week │    Game of Thrones            ││
│  │      │  Dune    │  Tonight   │    Marathon        │           ││
│  ├──────┼─────────────────────────────────────────────────────────┤│
│  │      │                                  │         │           ││
│  │ CNN  │        Breaking News Coverage    │ Anderson│  360°    ││
│  │      │                                  │ Cooper  │           ││
│  ├──────┼─────────────────────────────────────────────────────────┤│
│  │      │          │            │          │         │           ││
│  │ Sky  │ Premier  │ Post-Match │ La Liga  │ Serie A │ News     ││
│  │Sports│ League   │ Analysis   │ Live     │         │           ││
│  └──────┴─────────────────────────────────────────────────────────┘│
│                                                                     │
│  ◀ ─────────────────────  NOW  ───────────────────── ▶             │
│                                                                     │
├─────────────────────────────────────────────────────────────────────┤
│  [All] [Sports] [Movies] [News] [Entertainment] [Kids] [Favorites] │
└─────────────────────────────────────────────────────────────────────┘
```

**EPG Grid View Features:**

| Feature | Implementation |
|---------|----------------|
| Time slots | 30-minute increments |
| Time range | -2h to +24h from current |
| Current time | Red vertical line indicator |
| Program blocks | Span multiple slots if >30min |
| Channel column | Fixed (logo + name + fav star) |
| Scrolling | Horizontal: time, Vertical: channels |

**Navigation:**
- D-pad left/right: Move through time
- D-pad up/down: Change channel row
- Select: Opens program detail / starts playback
- Long-press: Add to favorites / Set reminder

**Category Tabs:**
- Horizontal tab bar at bottom of guide
- Filters visible channels by category
- "Favorites" tab shows only starred channels

**Filter Dropdown:**
- Sort: Alphabetical, Popularity, Recently Added
- Show: All, Live Only, HD Only
- Language filter

**Alternative View Toggle:**
- Icon button to switch between Grid/List view
- List view: Simpler, faster for low-power devices

### 2.3 Player Screen

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                                                                     │
│                                                                     │
│                         [VIDEO CONTENT]                             │
│                                                                     │
│                                                                     │
│                                                                     │
│                                                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘

OVERLAY (appears on any button press, auto-hides after 5s):

┌─────────────────────────────────────────────────────────────────────┐
│ ◀ Back                                                              │
│                                                                     │
│                                                                     │
│                                                                     │
│                                                                     │
│                                                                     │
│                                                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  ESPN                              ▶ LIVE    HD 1080p   ⭐   │  │
│  │  ──────────────────────────────────────────────────────────  │  │
│  │  NFL Sunday: Patriots vs Chiefs                              │  │
│  │  Started 45 min ago • Sports                                 │  │
│  │                                                              │  │
│  │  ████████████████░░░░░░░░░░░░░░░░░  Buffer: 98%             │  │
│  │                                                              │  │
│  │  ▲ CH+ 124 BBC One                                          │  │
│  │  ▼ CH- 122 Fox Sports                                       │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  [⏮] [⏪] [⏸/▶] [⏩] [⏭]    [🔊 Vol]  [⚙ Settings]  [📺 Guide] │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Minimal Playback Overlay:**

| Element | Position | Behavior |
|---------|----------|----------|
| Back button | Top-left | Returns to previous screen |
| Channel info | Bottom panel | Logo, name, quality badge, favorite toggle |
| Program info | Below channel | Current show, start time, category |
| Buffer indicator | Bottom | Progress bar with percentage |
| Channel preview | Above/below | Shows adjacent channels for quick switch |
| Controls | Bottom bar | Standard playback controls |

**Quick Channel Switching:**
- D-pad UP: Preview next channel (with 2s delay, auto-switches)
- D-pad DOWN: Preview previous channel
- Number keys (if available): Direct channel input
- Long-press UP/DOWN: Opens mini channel list overlay

**Quality Indicator:**
```
┌─────────────────┐
│  ● HD 1080p     │  Green dot = Good
│  ● HD 720p      │  Yellow dot = Fair  
│  ● SD 480p      │  Red dot = Poor
│  ○ Buffering... │  Hollow = Loading
└─────────────────┘
```

**Buffer Status:**
- Visual progress bar
- Percentage text
- Color coding: Green (>80%), Yellow (40-80%), Red (<40%)

**Auto-hide Behavior:**
- Overlay appears on any remote button press
- Hides after 5 seconds of inactivity
- Always visible while buffering
- Quick tap shows minimal info (channel + quality)
- Hold/second tap shows full overlay

### 2.4 Settings Screen

```
┌─────────────────────────────────────────────────────────────────────┐
│ ◀ Settings                                                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌────────────────────────────────────────────────────────────────┐│
│  │                                                                ││
│  │  ┌─────────────────┐  ┌──────────────────────────────────────┐││
│  │  │                 │  │                                      │││
│  │  │  📡 Scraper     │  │  SCRAPER SETTINGS                   │││
│  │  │  ────────────── │  │                                      │││
│  │  │  ▶ Playback     │  │  Auto-update channels               │││
│  │  │    Appearance   │  │  └─ ● Daily  ○ Weekly  ○ Manual     │││
│  │  │    Integrations │  │                                      │││
│  │  │    Data         │  │  Update time                         │││
│  │  │    About        │  │  └─ 3:00 AM (when idle)              │││
│  │  │                 │  │                                      │││
│  │  │                 │  │  [Update Now]          Last: 2h ago  │││
│  │  │                 │  │                                      │││
│  │  │                 │  │  Scraper source                      │││
│  │  │                 │  │  └─ acestream.lol ▼                  │││
│  │  │                 │  │                                      │││
│  │  │                 │  │  Include categories                  │││
│  │  │                 │  │  └─ [✓] Sports  [✓] Movies  [✓] News │││
│  │  │                 │  │                                      │││
│  │  └─────────────────┘  └──────────────────────────────────────┘││
│  │                                                                ││
│  └────────────────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Settings Categories:**

#### 📡 Scraper Settings
| Setting | Options | Default |
|---------|---------|---------|
| Auto-update schedule | Daily / Weekly / Manual | Daily |
| Update time | Time picker | 3:00 AM |
| Update Now button | Triggers immediate scrape | - |
| Scraper source | Dropdown of sources | acestream.lol |
| Category filters | Multi-select checkboxes | All enabled |
| Clear failed channels | Toggle | On |

#### ▶ Playback
| Setting | Options | Default |
|---------|---------|---------|
| Buffer size | Small (5s) / Medium (15s) / Large (30s) | Medium |
| Preferred quality | Auto / 1080p / 720p / 480p | Auto |
| Auto-play next | Toggle | Off |
| Hardware acceleration | Toggle | On |
| Audio output | System / Passthrough | System |
| Subtitle default | Off / Auto / Always ask | Auto |

#### 🎨 Appearance
| Setting | Options | Default |
|---------|---------|---------|
| Theme | Dark / AMOLED Black / Light | Dark |
| Accent color | Color picker (6 presets) | Blue |
| Layout density | Comfortable / Compact | Comfortable |
| Show channel numbers | Toggle | On |
| Animation speed | Normal / Reduced / Off | Normal |
| Clock format | 12h / 24h | System |

#### 🔗 Integrations
| Setting | Options | Default |
|---------|---------|---------|
| Real Debrid | Login / Status / Logout | Not connected |
| AceStream Engine | Status / Restart / Settings | Auto-detect |
| External player | None / VLC / MX Player | Built-in |
| Trakt.tv | Connect account | Not connected |

#### 💾 Data Management
| Setting | Options | Default |
|---------|---------|---------|
| Clear cache | Button (shows size) | - |
| Clear watch history | Button + confirmation | - |
| Export favorites | Button → JSON file | - |
| Import favorites | File picker | - |
| Backup settings | Export all settings | - |
| Restore settings | Import settings file | - |
| Reset to defaults | Button + confirmation | - |

#### ℹ️ About
| Item | Content |
|------|---------|
| App version | 1.0.0 (build 123) |
| Check for updates | Button |
| Changelog | Opens dialog |
| Open source licenses | Opens list |
| GitHub repo | Link |
| Support / Donate | Link to Ko-fi/GitHub Sponsors |
| Debug logs | Toggle + Export |

### 2.5 Category Browser

```
┌─────────────────────────────────────────────────────────────────────┐
│ ◀ Sports                                          🔍    Filter ▼   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  [All] [⚽ Football] [🏀 Basketball] [🎾 Tennis] [🏈 American] ... │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  LIVE NOW (12)                                   See All ▶  │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │   │
│  │  │ 🔴 LIVE │ │ 🔴 LIVE │ │ 🔴 LIVE │ │ 🔴 LIVE │           │   │
│  │  │ ESPN    │ │ Sky Sp  │ │ beIN    │ │ DAZN    │           │   │
│  │  │ NFL     │ │ PL      │ │ LaLiga  │ │ UFC     │           │   │
│  │  │ 1.2k 👁 │ │ 856 👁  │ │ 634 👁  │ │ 423 👁  │           │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  UPCOMING (5)                                    See All ▶  │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐                       │   │
│  │  │ ⏰ 2:00p│ │ ⏰ 4:30p│ │ ⏰ 7:00p│                       │   │
│  │  │ ESPN2   │ │ TNT     │ │ Fox     │                       │   │
│  │  │ NBA     │ │ NHL     │ │ NASCAR  │                       │   │
│  │  └─────────┘ └─────────┘ └─────────┘                       │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ALL SPORTS CHANNELS (47)                                   │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌───────  │   │
│  │  │ ESPN    │ │ ESPN2   │ │ ESPN+   │ │ ESPNU   │ │ ESPNew  │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └───────  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
├─────────────────────────────────────────────────────────────────────┤
│  🏠 Home   📺 Guide   📁 Categories   ⭐ Favorites   🔍 Search     │
└─────────────────────────────────────────────────────────────────────┘
```

**Sports Subcategories:**
- ⚽ Football/Soccer
- 🏀 Basketball  
- 🎾 Tennis
- 🏈 American Football
- ⚾ Baseball
- 🏒 Hockey
- 🏎️ Motorsports
- 🥊 Combat Sports (MMA/Boxing)
- 🏌️ Golf
- 🚴 Cycling
- 🎿 Winter Sports
- ⚽ Cricket
- 🏉 Rugby

**Live vs VOD Distinction:**
```
┌───────────────┐     ┌───────────────┐
│   🔴 LIVE     │     │   📺 VOD      │
│   ─────────   │     │   ─────────   │
│   Red badge   │     │   Blue badge  │
│   Viewer count│     │   Duration    │
│   "Now" time  │     │   "2h 15m"    │
└───────────────┘     └───────────────┘
```

**Country/Region Filters:**
- Dropdown or expandable filter panel
- Options: All Regions, USA, UK, Europe, Asia, Latin America
- Remembers last selection

---

## 3. Remote Control Navigation

### 3.1 D-pad Navigation Patterns

```
Standard Android TV Remote Layout:
        
         [Mic]
           │
    ┌──────┴──────┐
    │             │
    │     [▲]     │
    │ [◀] [●] [▶] │
    │     [▼]     │
    │             │
    └─────────────┘
    [◀Back]  [Home]
    
    [<<] [▶⏸] [>>]
    
    [Vol+]  [Mute]
    [Vol-]
```

**Navigation Rules:**

| Context | D-pad Action | Result |
|---------|--------------|--------|
| Any grid | ▲▼◀▶ | Move focus between items |
| Horizontal row | ◀▶ | Scroll within row |
| Horizontal row | ▼ | Move to next row |
| End of row | ▶ | Wrap to next row (optional) OR stop |
| Top of screen | ▲ | Focus top nav / do nothing |
| Player | ▲▼ | Channel up/down |
| Player | ◀▶ | Seek (if DVR supported) |
| Dialog | ◀▶ | Switch between buttons |

### 3.2 Focus Management

**Focus States:**
```css
/* Unfocused */
border: 2px solid transparent;
transform: scale(1.0);

/* Focused */
border: 2px solid #FFFFFF;
transform: scale(1.05);
box-shadow: 0 4px 20px rgba(0,0,0,0.4);
background: linear-gradient(rgba(255,255,255,0.1));

/* Pressed */
transform: scale(0.98);
background: rgba(255,255,255,0.2);
```

**Focus Memory:**
- Remember last focused item when leaving/returning to screen
- Smart focus: Jump to most relevant item (e.g., "Continue Watching" if available)

**Focus Traps:**
- Dialogs trap focus within themselves
- Settings categories: left panel ↔ right content with D-pad
- Player overlay: focus on controls bar

### 3.3 Long-press Actions

| Screen | Long-press Target | Action |
|--------|-------------------|--------|
| Any channel card | Channel | Context menu (Favorite, Details, Play) |
| Favorites | Channel | Remove from favorites / Reorder |
| TV Guide | Program | Set reminder / Add to favorites |
| Search results | Result | Same as channel card |
| Player | Screen | Quick settings overlay |
| Settings | Setting item | Reset to default |

**Long-press Timing:** 800ms (Android TV standard)

**Context Menu Design:**
```
┌─────────────────────┐
│  ESPN               │
├─────────────────────┤
│  ▶ Play Now         │
│  ⭐ Add to Favorites │
│  ℹ️ Channel Info     │
│  🔔 Set Reminder     │
└─────────────────────┘
```

### 3.4 Quick Shortcuts

| Button | Global Action | Player Action |
|--------|---------------|---------------|
| Search/Mic | Opens search with voice | - |
| Menu (≡) | Opens settings overlay | Opens playback settings |
| Back | Previous screen | Exit player / Show overlay |
| Home | Android TV home | Android TV home |
| Play/Pause | - | Toggle playback |
| Rewind | - | Seek back 10s (if supported) |
| Fast Forward | - | Seek forward 10s (if supported) |
| Number keys | Direct channel input | Direct channel input |

**Number Key Channel Input:**
- Press any number → Shows channel input overlay
- Type up to 4 digits
- Auto-confirms after 2 seconds OR press Select
- Shows channel preview while typing

---

## 4. Visual Design Guidelines

### 4.1 Color Scheme

**Primary Palette (Dark Theme - Default):**

| Purpose | Color | Hex | Usage |
|---------|-------|-----|-------|
| Background Primary | Charcoal | `#121212` | Main app background |
| Background Secondary | Dark Gray | `#1E1E1E` | Cards, panels |
| Background Elevated | Gray | `#2D2D2D` | Focused items, dialogs |
| Text Primary | White | `#FFFFFF` | Headlines, important text |
| Text Secondary | Light Gray | `#B3B3B3` | Descriptions, metadata |
| Text Disabled | Gray | `#666666` | Inactive items |
| Accent Primary | Blue | `#2196F3` | Selected items, buttons |
| Accent Secondary | Light Blue | `#64B5F6` | Links, secondary actions |
| Live Indicator | Red | `#F44336` | LIVE badges |
| Success | Green | `#4CAF50` | Buffer OK, connected |
| Warning | Amber | `#FFC107` | Buffer warning |
| Error | Red | `#EF5350` | Errors, disconnected |

**AMOLED Black Theme:**
- Background Primary: `#000000`
- Background Secondary: `#0D0D0D`
- All other colors same as Dark

**Light Theme (Optional):**
- Background Primary: `#FAFAFA`
- Background Secondary: `#FFFFFF`
- Text Primary: `#212121`
- Text Secondary: `#757575`

### 4.2 Typography

**Font Family:** Product Sans (Google), fallback: Roboto

**Type Scale (10-foot viewing distance):**

| Style | Size | Weight | Line Height | Usage |
|-------|------|--------|-------------|-------|
| Display | 48sp | Medium | 56sp | Hero text, welcome |
| Headline 1 | 32sp | Medium | 40sp | Screen titles |
| Headline 2 | 24sp | Medium | 32sp | Section titles |
| Title | 20sp | Medium | 28sp | Card titles, channel names |
| Body 1 | 18sp | Regular | 26sp | Descriptions |
| Body 2 | 16sp | Regular | 24sp | Secondary info |
| Caption | 14sp | Regular | 20sp | Timestamps, metadata |
| Button | 16sp | Medium | 24sp | All caps optional |

**Legibility Rules:**
- Minimum text size: 14sp
- Maximum line length: 60 characters
- High contrast: Always meet WCAG AA (4.5:1 ratio)

### 4.3 Icon Style

**Style:** Material Design Outlined (consistent with Android TV)

**Sizes:**
| Context | Size | Padding |
|---------|------|---------|
| Navigation bar | 24dp | 12dp |
| Action buttons | 20dp | 10dp |
| Category icons | 48dp | 16dp |
| Status indicators | 12dp | 4dp |

**Custom Icons Needed:**
- App logo/launcher icon
- AceStream engine status
- Buffer indicator
- Quality badges (HD, FHD, 4K, SD)
- Live indicator (pulsing dot)

### 4.4 Animation/Transitions

**Principles:**
- Keep animations under 300ms for responsiveness
- Use deceleration curves (elements entering)
- Use acceleration curves (elements leaving)
- Reduce or disable for "Reduced motion" setting

**Standard Animations:**

| Animation | Duration | Curve | Usage |
|-----------|----------|-------|-------|
| Focus scale | 150ms | ease-out | Card focus |
| Page transition | 250ms | ease-in-out | Screen changes |
| Fade in | 200ms | ease-out | Content loading |
| Slide up | 200ms | decelerate | Dialogs, overlays |
| Collapse/Expand | 200ms | ease-in-out | Accordions |

**Player Overlay:**
- Fade in: 150ms
- Auto-hide: Fade out 300ms after 5s idle

### 4.5 Loading States

**Skeleton Loading:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                   │
│  │ ▓▓▓▓▓▓▓ │ │ ▓▓▓▓▓▓▓ │ │ ▓▓▓▓▓▓▓ │ │ ▓▓▓▓▓▓▓ │   ← Shimmer      │
│  │ ▓▓▓▓▓   │ │ ▓▓▓▓▓   │ │ ▓▓▓▓▓   │ │ ▓▓▓▓▓   │     animation    │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Spinner:**
- Use Material Design circular progress
- Size: 48dp for full-screen, 24dp for inline
- Color: Accent Primary

**Progress Bar (Scraping):**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                    Updating Channel List...                         │
│                                                                     │
│           ████████████████████░░░░░░░░░░░░░░░░░  67%               │
│                                                                     │
│                    Found 234 channels                               │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Empty States:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                         ┌─────────┐                                 │
│                         │   📺    │                                 │
│                         │   🔍    │                                 │
│                         └─────────┘                                 │
│                                                                     │
│                   No channels found                                 │
│                                                                     │
│           Try updating the channel list or                          │
│              check your scraper settings                            │
│                                                                     │
│                      [Update Now]                                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 5. Premium Features Brainstorm

### 5.1 Open Source "Premium" Model

Since this is open-source, "premium" doesn't mean paywall. Instead, consider:

**Tier 1: Core (Free, Always)**
- All basic functionality
- Single scraper source
- Standard themes
- Local favorites

**Tier 2: Enhanced (Donation Unlocked)**
- Multiple scraper sources
- Custom themes / accent colors
- Cloud sync (favorites, settings)
- Priority in feature requests

**Tier 3: Supporter Badge**
- Visible in About screen
- Name in contributors list
- Beta access to new features

### 5.2 Potential "Advanced" Features

| Feature | Complexity | Value |
|---------|------------|-------|
| Cloud sync | High | Sync across devices |
| Multiple profiles | Medium | Family sharing |
| Recording/DVR | Very High | Record streams |
| Picture-in-Picture | Medium | Multitask |
| Custom scraper plugins | High | Power users |
| Stream health monitoring | Medium | Better reliability |
| Parental controls | Medium | Family feature |
| Voice control extensions | Medium | Accessibility |
| Chromecast support | Medium | Cast to other TVs |
| Multiple audio tracks | Low | Language options |

### 5.3 Donation/Support Model

**Options:**
1. **GitHub Sponsors** - Monthly tiers ($1, $5, $10)
2. **Ko-fi** - One-time donations
3. **Buy Me a Coffee** - Similar to Ko-fi
4. **Patreon** - For ongoing development updates

**In-App Implementation:**
- Settings → About → Support Development
- Non-intrusive banner after 10 app opens
- "Thank you" Easter egg for donors

**What Donations Unlock:**
- Nothing mandatory (keep everything free)
- Optional: Remove "Support Development" banner
- Optional: Unlock beta features early
- Optional: Custom accent color palette

---

## 6. Accessibility Considerations

### 6.1 TalkBack Support

**Content Descriptions:**
Every interactive element needs:
```kotlin
// Example
channelCard.contentDescription = "ESPN, showing NFL Sunday, Live, 1.2k viewers"
favoriteButton.contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
```

**Focus Order:**
- Logical left-to-right, top-to-bottom
- Skip decorative elements
- Group related items with `accessibilityTraversalBefore/After`

**Announcements:**
- Screen changes: "Home screen, 4 sections"
- Loading: "Loading channels, please wait"
- Errors: "Error: Stream unavailable. Press select to retry"

### 6.2 High Contrast Mode

**Enable via:** Settings → Appearance → High Contrast

**Changes Applied:**
| Element | Normal | High Contrast |
|---------|--------|---------------|
| Text | #B3B3B3 | #FFFFFF |
| Focus border | 2px | 4px |
| Card borders | None | 1px white |
| Icons | Outlined | Filled |
| Background | #121212 | #000000 |

### 6.3 Text Scaling

**Support System Font Scaling:**
- Test at 1.0x, 1.5x, 2.0x scales
- Use `sp` for all text sizes
- Ensure layouts don't break at 2.0x
- Critical text remains single-line or truncates gracefully

**In-App Text Size:**
- Settings → Appearance → Text Size
- Options: Small, Default, Large, Extra Large
- Scales all text by 0.85x, 1.0x, 1.15x, 1.3x

### 6.4 Additional A11y Features

| Feature | Implementation |
|---------|----------------|
| Reduce motion | Disables all animations |
| Audio descriptions | Show/hide in player settings |
| Captions | Always available if stream supports |
| Color blind modes | Optional filters (deuteranopia, protanopia, tritanopia) |
| Button repeat | D-pad repeat for faster scrolling |
| Focus timeout | Extend auto-dismiss for overlays (20s option) |

---

## 7. Error States & Edge Cases

### 7.1 No Internet Connection

**Detection:** Monitor connectivity state

**UI Response:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                         ┌─────────┐                                 │
│                         │   📡    │                                 │
│                         │   ✕     │                                 │
│                         └─────────┘                                 │
│                                                                     │
│                  No Internet Connection                             │
│                                                                     │
│           Check your network settings and try again                 │
│                                                                     │
│                [Open Network Settings]  [Retry]                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Behavior:**
- Show cached content if available (with "Offline" badge)
- Disable features requiring network
- Auto-retry every 30 seconds
- Show toast when connection restored

### 7.2 Stream Unavailable

**Scenarios:**
- Channel offline
- AceStream peers unavailable
- Geographic restriction
- Stream URL expired

**UI Response:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                                                                     │
│                                                                     │
│                              ⚠️                                     │
│                                                                     │
│                    Stream Unavailable                               │
│                                                                     │
│              This channel is currently offline.                     │
│              It may come back later.                                │
│                                                                     │
│            [Try Again]  [Report Issue]  [Back to Guide]             │
│                                                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Auto-Recovery:**
- Retry up to 3 times automatically
- Increase retry delay (2s, 5s, 10s)
- After 3 failures, show error with manual retry

### 7.3 Scraper Failed

**Scenarios:**
- Source website changed
- Network timeout
- Invalid response

**UI Response:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  ⚠️ Channel Update Failed                                          │
│                                                                     │
│  Could not fetch channels from acestream.lol                        │
│  Error: Connection timeout                                          │
│                                                                     │
│  Your existing channels are still available.                        │
│                                                                     │
│  [Retry]  [Try Different Source]  [Dismiss]                         │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Behavior:**
- Keep existing channel data (graceful degradation)
- Log error for debugging
- Suggest alternative scraper sources
- Don't block app usage

### 7.4 AceStream Engine Not Installed

**Detection:** Check for AceStream app/service on launch

**First-Time Flow:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                         ┌─────────┐                                 │
│                         │ ACESTREAM│                                │
│                         │  ENGINE │                                 │
│                         └─────────┘                                 │
│                                                                     │
│               AceStream Engine Required                             │
│                                                                     │
│     This app requires the AceStream Engine to play streams.         │
│     It's free and takes about 2 minutes to install.                 │
│                                                                     │
│              [Install from Play Store]                              │
│                                                                     │
│                   [Learn More]                                      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Engine Crashed:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  ⚠️ AceStream Engine Error                                         │
│                                                                     │
│  The streaming engine stopped unexpectedly.                         │
│                                                                     │
│  [Restart Engine]  [Open AceStream App]  [Continue Without]         │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 7.5 Additional Edge Cases

| Case | Response |
|------|----------|
| Empty search results | "No results for [query]. Try different keywords." |
| No favorites yet | "No favorites yet. Long-press a channel to add it." |
| Playback buffer underrun | Auto-lower quality + show toast |
| Storage full | Warn when cache exceeds threshold |
| App update available | Non-blocking banner with "Update" button |
| Session timeout | Auto-refresh tokens silently |
| Concurrent stream limit | "Playing on another device. Switch here?" |

---

## 8. Menu Structure

### 8.1 Main Navigation

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  Main Menu (Bottom Navigation Bar)                                  │
│  ═══════════════════════════════════                                │
│                                                                     │
│  🏠 Home                                                            │
│  ├── Featured/Trending                                              │
│  ├── Continue Watching                                              │
│  ├── Category Rows (Sports, Movies, News...)                        │
│  └── Recently Added                                                 │
│                                                                     │
│  📺 TV Guide                                                        │
│  ├── EPG Grid View                                                  │
│  ├── Timeline Navigation                                            │
│  ├── Category Tabs                                                  │
│  └── List View Toggle                                               │
│                                                                     │
│  📁 Categories                                                      │
│  ├── 🏆 Sports                                                      │
│  │   ├── ⚽ Football                                                │
│  │   ├── 🏀 Basketball                                              │
│  │   ├── 🎾 Tennis                                                  │
│  │   ├── 🏈 American Football                                       │
│  │   ├── 🏒 Hockey                                                  │
│  │   └── [More...]                                                  │
│  ├── 🎬 Movies                                                      │
│  │   ├── Action                                                     │
│  │   ├── Comedy                                                     │
│  │   ├── Drama                                                      │
│  │   ├── Horror                                                     │
│  │   └── Documentary                                                │
│  ├── 📰 News                                                        │
│  │   ├── By Region                                                  │
│  │   └── By Language                                                │
│  ├── 🎭 Entertainment                                               │
│  ├── 🧒 Kids                                                        │
│  ├── 🎵 Music                                                       │
│  └── 📚 Documentary                                                 │
│                                                                     │
│  ⭐ Favorites                                                       │
│  ├── Grid of favorited channels                                     │
│  ├── Sort options                                                   │
│  └── Edit mode                                                      │
│                                                                     │
│  🔍 Search                                                          │
│  ├── Voice input                                                    │
│  ├── Keyboard input                                                 │
│  ├── Recent searches                                                │
│  └── Live results                                                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 8.2 Settings Menu

```
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  ⚙️ Settings                                                        │
│  ════════════                                                       │
│                                                                     │
│  📡 Scraper                                                         │
│  ├── Auto-update schedule                                           │
│  ├── Update time                                                    │
│  ├── Update now                                                     │
│  ├── Scraper source                                                 │
│  └── Category filters                                               │
│                                                                     │
│  ▶️ Playback                                                        │
│  ├── Buffer size                                                    │
│  ├── Preferred quality                                              │
│  ├── Hardware acceleration                                          │
│  ├── Audio output                                                   │
│  └── Subtitle default                                               │
│                                                                     │
│  🎨 Appearance                                                      │
│  ├── Theme (Dark/AMOLED/Light)                                      │
│  ├── Accent color                                                   │
│  ├── Layout density                                                 │
│  ├── Show channel numbers                                           │
│  └── Animation speed                                                │
│                                                                     │
│  ♿ Accessibility                                                    │
│  ├── Text size                                                      │
│  ├── High contrast                                                  │
│  ├── Reduce motion                                                  │
│  └── Focus timeout                                                  │
│                                                                     │
│  🔗 Integrations                                                    │
│  ├── Real Debrid                                                    │
│  ├── AceStream Engine                                               │
│  ├── External player                                                │
│  └── Trakt.tv                                                       │
│                                                                     │
│  💾 Data                                                            │
│  ├── Clear cache                                                    │
│  ├── Clear watch history                                            │
│  ├── Export favorites                                               │
│  ├── Import favorites                                               │
│  ├── Backup settings                                                │
│  ├── Restore settings                                               │
│  └── Reset to defaults                                              │
│                                                                     │
│  ℹ️ About                                                           │
│  ├── Version info                                                   │
│  ├── Check for updates                                              │
│  ├── Changelog                                                      │
│  ├── Open source licenses                                           │
│  ├── GitHub                                                         │
│  ├── Support / Donate                                               │
│  └── Debug logs                                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 8.3 Context Menus

**Channel Card Context Menu:**
```
┌─────────────────────────┐
│  ESPN                   │
├─────────────────────────┤
│  ▶ Play Now             │
│  ⭐ Add to Favorites    │
│  ℹ️ Channel Info        │
│  🔔 Set Reminder        │
│  📋 Copy Stream Link    │
└─────────────────────────┘
```

**Player Quick Menu:**
```
┌─────────────────────────┐
│  Quick Settings         │
├─────────────────────────┤
│  Quality: Auto ▼        │
│  Audio: Track 1 ▼       │
│  Subtitles: Off ▼       │
├─────────────────────────┤
│  ⭐ Favorite            │
│  📺 Back to Guide       │
│  ⚙️ Full Settings       │
└─────────────────────────┘
```

---

## 9. Wireframes & Mockup Notes

### 9.1 Component Library

**Card Variants:**
```
Standard Channel Card (160x90dp):
┌─────────────────┐
│   [Channel      │
│    Logo]        │
│                 │
├─────────────────┤
│ ESPN      🔴 HD │
└─────────────────┘

Featured Card (320x180dp):
┌───────────────────────────────────┐
│                                   │
│   [Large Thumbnail/              │
│    Current Frame]                │
│                     🔴 LIVE      │
├───────────────────────────────────┤
│ ESPN  •  NFL Sunday: Patriots... │
│ 1.2k watching                    │
└───────────────────────────────────┘

Category Card (200x120dp):
┌─────────────────────┐
│                     │
│      [Icon]         │
│        🏆           │
│                     │
│      Sports         │
│    (45 channels)    │
└─────────────────────┘
```

### 9.2 Responsive Breakpoints

| Screen Size | Layout Adjustments |
|-------------|-------------------|
| 1080p (Full HD) | 5 cards per row |
| 720p (HD) | 4 cards per row |
| 4K | 7 cards per row |

### 9.3 Implementation Notes

**Recommended Libraries:**
- Leanback library (Android TV native components)
- Glide for image loading
- ExoPlayer for playback
- Room for local database

**Key Leanback Components:**
- `BrowseSupportFragment` - Home screen rows
- `SearchSupportFragment` - Search with voice
- `DetailsFragment` - Channel/Program details
- `PlaybackSupportFragment` - Video player controls

**Testing Recommendations:**
- Test with Google Streamer 4K (primary target)
- Test with Shield TV (high-end)
- Test with Mi Box S (mid-range)
- Test with Fire TV Stick (low-end, if supporting)

---

## Appendix A: Design Checklist

### Pre-Development
- [ ] Define brand identity (name, logo, colors)
- [ ] Create high-fidelity mockups for key screens
- [ ] Document component specifications
- [ ] Plan accessibility from day one

### Development
- [ ] Implement Leanback navigation patterns
- [ ] Test all focus states
- [ ] Verify TalkBack compatibility
- [ ] Test on multiple device tiers

### Pre-Launch
- [ ] User testing with TV remotes
- [ ] Performance profiling
- [ ] A11y audit
- [ ] Localization preparation

---

## Appendix B: Inspirations & References

**Similar Apps to Study:**
- Netflix (Navigation, loading states)
- YouTube TV (EPG design)
- Plex (Media organization)
- Steam Link (Gaming-focused TV UI)
- Tivimate (IPTV reference)

**Design Resources:**
- [Android TV Design Guidelines](https://developer.android.com/design/ui/tv)
- [Material Design for TV](https://material.io/design)
- [Leanback Library Documentation](https://developer.android.com/training/tv/playback)

---

*Document Version: 1.0*  
*Last Updated: February 8, 2026*  
*Author: Claude (UX/UI Focus)*
