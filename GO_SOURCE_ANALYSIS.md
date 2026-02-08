# Go Source Analysis: m3u_gen_acestream

> Source: https://github.com/SCP002/m3u_gen_acestream
> Cloned to: /tmp/m3u_gen_acestream

## Overview

~1842 lines of Go code across 7 source files (plus 1147 lines of tests).

## Files to Port

### 1. engine.go (200 lines) → AceStreamEngineClient.kt

**Location:** `/tmp/m3u_gen_acestream/src/acestream/engine.go`

**Core Functions:**
- `NewEngine(addr string)` - Constructor with page size 200
- `WaitForConnection(ctx)` - Polls `/webui/api/service?method=get_version` every 5s until success
- `SearchAll(ctx)` - Paginated search, iterates until results < pageSize
- `searchAtPage(ctx, page)` - GET `/search?page_size=200&page=N`

**Data Models:**
```go
type SearchResult struct {
    Items []Item
    Name  AnyStr  // Can be string OR number, needs custom unmarshal
    Icons []Icon
}

type Item struct {
    Status                int
    Languages             []string
    Name                  string
    Countries             []string
    Infohash              string
    ChannelID             int
    AvailabilityUpdatedAt int64
    Availability          float64
    Categories            []string
}

type Icon struct {
    URL  string
    Type int
}
```

**Note:** `AnyStr` has custom JSON unmarshaller to handle engine returning numbers for the name field.

### 2. generator.go (506 lines) → ChannelFilter.kt + CategoryMapper.kt

**Location:** `/tmp/m3u_gen_acestream/src/m3u/generator.go`

**Filter Pipeline (in order):**
1. `remapCategoryToCategory` - Regex-based category rename
2. `remapNameToCategories` - Infer category from channel name if empty
3. `filterByStatus` - Keep only channels with status in whitelist
4. `filterByAvailability` - Keep if availability >= threshold
5. `filterByAvailabilityUpdateTime` - Keep if updated within threshold
6. `filterByCategories` - Category whitelist + blacklist (with strict mode)
7. `filterByLanguages` - Language whitelist + blacklist (with strict mode)
8. `filterByCountries` - Country whitelist + blacklist (with strict mode)
9. `filterByName` - Regex include patterns + blacklist patterns
10. `removeDead` - Parallel dead source checking (separate function)

**Key Patterns:**
- Uses `regexp2` library (RE2 compatible) for regex
- `strict` mode: ALL filters must match vs ANY filter matches
- Blacklists run after whitelists
- Categories/languages/countries default to [""] if empty (to match empty filters)

### 3. checker.go (63 lines) → StreamChecker.kt

**Location:** `/tmp/m3u_gen_acestream/src/acestream/checker.go`

**Core Function:**
```go
func (c Checker) IsAvailable(link string, timeout time.Duration, analyzeMpegTs bool) error
```

**Logic:**
1. HTTP GET with timeout
2. Check status code < 400
3. Read up to 10 × 188 bytes (10 TS packets)
4. If `analyzeMpegTs=true`: Use `ts.PktStreamReader` to validate MPEG-TS sync
5. Return nil (alive) or error (dead)

**TS Packet Validation:**
- Uses `github.com/ziutek/dvb/ts` library
- Packet length: 188 bytes
- Sync byte: 0x47 at start of each packet

### 4. config.go (531 lines) → Room Entities + DataStore

**Location:** `/tmp/m3u_gen_acestream/src/config/config.go`

**Playlist Config Fields:**
```go
type Playlist struct {
    OutputPath                   string
    HeaderTemplate               string
    EntryTemplate                string
    CategoryRxToCategoryMap      map[string]string    // regex → category
    NameRxToCategoriesMap        map[string][]string  // regex → categories
    NameRxFilter                 []string             // include patterns
    NameRxBlacklist              []string             // exclude patterns
    CategoriesFilter             []string             // whitelist
    CategoriesFilterStrict       bool
    CategoriesBlacklist          []string
    LanguagesFilter              []string
    LanguagesFilterStrict        bool
    LanguagesBlacklist           []string
    CountriesFilter              []string
    CountriesFilterStrict        bool
    CountriesBlacklist           []string
    StatusFilter                 []int
    AvailabilityThreshold        float64
    AvailabilityUpdatedThreshold time.Duration
    RemoveDeadSources            *bool
    UseMpegTsAnalyzer            *bool
    CheckRespTimeout             *time.Duration
    RemoveDeadLinkTemplate       *string
    RemoveDeadWorkers            *int
}
```

## Kotlin Port Mapping

| Go File | Kotlin File | Complexity |
|---------|-------------|------------|
| engine.go | AceStreamEngineClient.kt | Low |
| generator.go (filter) | ChannelFilter.kt | Medium |
| generator.go (remap) | CategoryMapper.kt | Low |
| checker.go | StreamChecker.kt | Low |
| config.go | FilterRuleEntity.kt + SettingsDataStore.kt | Medium |

## Key Differences for Kotlin

1. **Async:** Use `suspend fun` with Ktor Client instead of `context.Context`
2. **Regex:** Use Kotlin `Regex` with `RegexOption.IGNORE_CASE`
3. **JSON:** Use kotlinx.serialization with custom serializer for AnyStr
4. **TS Validation:** Manual sync byte check (no dvb/ts library in Kotlin)

## Test Data

The Go repo has 1147 lines of tests in `generator_test.go` with test fixtures. Use these to validate the Kotlin port.
