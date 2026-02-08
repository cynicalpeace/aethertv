package com.aethertv.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AetherTvDatabase_Impl : AetherTvDatabase() {
  private val _channelDao: Lazy<ChannelDao> = lazy {
    ChannelDao_Impl(this)
  }

  private val _epgDao: Lazy<EpgDao> = lazy {
    EpgDao_Impl(this)
  }

  private val _favoriteDao: Lazy<FavoriteDao> = lazy {
    FavoriteDao_Impl(this)
  }

  private val _watchHistoryDao: Lazy<WatchHistoryDao> = lazy {
    WatchHistoryDao_Impl(this)
  }

  private val _filterRuleDao: Lazy<FilterRuleDao> = lazy {
    FilterRuleDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "d8fa1f30d4ea035570d4be1805af37be", "79c9e6fb7f3d365abf255ea0d694e851") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `channels` (`infohash` TEXT NOT NULL, `name` TEXT NOT NULL, `categories` TEXT NOT NULL, `languages` TEXT NOT NULL, `countries` TEXT NOT NULL, `iconUrl` TEXT, `status` INTEGER NOT NULL, `availability` REAL NOT NULL, `lastScrapedAt` INTEGER NOT NULL, `isVerified` INTEGER, `verifiedQuality` TEXT, `lastVerifiedAt` INTEGER, `verifiedPeerCount` INTEGER, `epgChannelId` TEXT, PRIMARY KEY(`infohash`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`infohash` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`infohash`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `watch_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `infohash` TEXT NOT NULL, `watchedAt` INTEGER NOT NULL, `durationSeconds` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `epg_programs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `channelId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `category` TEXT, `iconUrl` TEXT)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_epg_programs_channelId_startTime` ON `epg_programs` (`channelId`, `startTime`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `epg_channels` (`xmltvId` TEXT NOT NULL, `displayName` TEXT NOT NULL, `iconUrl` TEXT, `language` TEXT, PRIMARY KEY(`xmltvId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `filter_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `pattern` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `category_remap_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sourcePattern` TEXT NOT NULL, `targetCategory` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd8fa1f30d4ea035570d4be1805af37be')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `channels`")
        connection.execSQL("DROP TABLE IF EXISTS `favorites`")
        connection.execSQL("DROP TABLE IF EXISTS `watch_history`")
        connection.execSQL("DROP TABLE IF EXISTS `epg_programs`")
        connection.execSQL("DROP TABLE IF EXISTS `epg_channels`")
        connection.execSQL("DROP TABLE IF EXISTS `filter_rules`")
        connection.execSQL("DROP TABLE IF EXISTS `category_remap_rules`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsChannels: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChannels.put("infohash", TableInfo.Column("infohash", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("categories", TableInfo.Column("categories", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("languages", TableInfo.Column("languages", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("countries", TableInfo.Column("countries", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("iconUrl", TableInfo.Column("iconUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("status", TableInfo.Column("status", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("availability", TableInfo.Column("availability", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("lastScrapedAt", TableInfo.Column("lastScrapedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("isVerified", TableInfo.Column("isVerified", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("verifiedQuality", TableInfo.Column("verifiedQuality", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("lastVerifiedAt", TableInfo.Column("lastVerifiedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("verifiedPeerCount", TableInfo.Column("verifiedPeerCount", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChannels.put("epgChannelId", TableInfo.Column("epgChannelId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChannels: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChannels: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChannels: TableInfo = TableInfo("channels", _columnsChannels, _foreignKeysChannels,
            _indicesChannels)
        val _existingChannels: TableInfo = read(connection, "channels")
        if (!_infoChannels.equals(_existingChannels)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |channels(com.aethertv.data.local.entity.ChannelEntity).
              | Expected:
              |""".trimMargin() + _infoChannels + """
              |
              | Found:
              |""".trimMargin() + _existingChannels)
        }
        val _columnsFavorites: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavorites.put("infohash", TableInfo.Column("infohash", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("addedAt", TableInfo.Column("addedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("sortOrder", TableInfo.Column("sortOrder", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavorites: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavorites: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavorites: TableInfo = TableInfo("favorites", _columnsFavorites,
            _foreignKeysFavorites, _indicesFavorites)
        val _existingFavorites: TableInfo = read(connection, "favorites")
        if (!_infoFavorites.equals(_existingFavorites)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorites(com.aethertv.data.local.entity.FavoriteEntity).
              | Expected:
              |""".trimMargin() + _infoFavorites + """
              |
              | Found:
              |""".trimMargin() + _existingFavorites)
        }
        val _columnsWatchHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWatchHistory.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWatchHistory.put("infohash", TableInfo.Column("infohash", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWatchHistory.put("watchedAt", TableInfo.Column("watchedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWatchHistory.put("durationSeconds", TableInfo.Column("durationSeconds", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWatchHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWatchHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWatchHistory: TableInfo = TableInfo("watch_history", _columnsWatchHistory,
            _foreignKeysWatchHistory, _indicesWatchHistory)
        val _existingWatchHistory: TableInfo = read(connection, "watch_history")
        if (!_infoWatchHistory.equals(_existingWatchHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |watch_history(com.aethertv.data.local.entity.WatchHistoryEntity).
              | Expected:
              |""".trimMargin() + _infoWatchHistory + """
              |
              | Found:
              |""".trimMargin() + _existingWatchHistory)
        }
        val _columnsEpgPrograms: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEpgPrograms.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("channelId", TableInfo.Column("channelId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("description", TableInfo.Column("description", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("startTime", TableInfo.Column("startTime", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("endTime", TableInfo.Column("endTime", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("category", TableInfo.Column("category", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgPrograms.put("iconUrl", TableInfo.Column("iconUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEpgPrograms: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEpgPrograms: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEpgPrograms.add(TableInfo.Index("index_epg_programs_channelId_startTime", false,
            listOf("channelId", "startTime"), listOf("ASC", "ASC")))
        val _infoEpgPrograms: TableInfo = TableInfo("epg_programs", _columnsEpgPrograms,
            _foreignKeysEpgPrograms, _indicesEpgPrograms)
        val _existingEpgPrograms: TableInfo = read(connection, "epg_programs")
        if (!_infoEpgPrograms.equals(_existingEpgPrograms)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |epg_programs(com.aethertv.data.local.entity.EpgProgramEntity).
              | Expected:
              |""".trimMargin() + _infoEpgPrograms + """
              |
              | Found:
              |""".trimMargin() + _existingEpgPrograms)
        }
        val _columnsEpgChannels: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEpgChannels.put("xmltvId", TableInfo.Column("xmltvId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgChannels.put("displayName", TableInfo.Column("displayName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgChannels.put("iconUrl", TableInfo.Column("iconUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEpgChannels.put("language", TableInfo.Column("language", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEpgChannels: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEpgChannels: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoEpgChannels: TableInfo = TableInfo("epg_channels", _columnsEpgChannels,
            _foreignKeysEpgChannels, _indicesEpgChannels)
        val _existingEpgChannels: TableInfo = read(connection, "epg_channels")
        if (!_infoEpgChannels.equals(_existingEpgChannels)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |epg_channels(com.aethertv.data.local.entity.EpgChannelEntity).
              | Expected:
              |""".trimMargin() + _infoEpgChannels + """
              |
              | Found:
              |""".trimMargin() + _existingEpgChannels)
        }
        val _columnsFilterRules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFilterRules.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFilterRules.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFilterRules.put("pattern", TableInfo.Column("pattern", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFilterRules.put("isEnabled", TableInfo.Column("isEnabled", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFilterRules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFilterRules: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFilterRules: TableInfo = TableInfo("filter_rules", _columnsFilterRules,
            _foreignKeysFilterRules, _indicesFilterRules)
        val _existingFilterRules: TableInfo = read(connection, "filter_rules")
        if (!_infoFilterRules.equals(_existingFilterRules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |filter_rules(com.aethertv.data.local.entity.FilterRuleEntity).
              | Expected:
              |""".trimMargin() + _infoFilterRules + """
              |
              | Found:
              |""".trimMargin() + _existingFilterRules)
        }
        val _columnsCategoryRemapRules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCategoryRemapRules.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategoryRemapRules.put("sourcePattern", TableInfo.Column("sourcePattern", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCategoryRemapRules.put("targetCategory", TableInfo.Column("targetCategory", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCategoryRemapRules.put("isEnabled", TableInfo.Column("isEnabled", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCategoryRemapRules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCategoryRemapRules: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCategoryRemapRules: TableInfo = TableInfo("category_remap_rules",
            _columnsCategoryRemapRules, _foreignKeysCategoryRemapRules, _indicesCategoryRemapRules)
        val _existingCategoryRemapRules: TableInfo = read(connection, "category_remap_rules")
        if (!_infoCategoryRemapRules.equals(_existingCategoryRemapRules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |category_remap_rules(com.aethertv.data.local.entity.CategoryRemapRuleEntity).
              | Expected:
              |""".trimMargin() + _infoCategoryRemapRules + """
              |
              | Found:
              |""".trimMargin() + _existingCategoryRemapRules)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "channels", "favorites",
        "watch_history", "epg_programs", "epg_channels", "filter_rules", "category_remap_rules")
  }

  public override fun clearAllTables() {
    super.performClear(false, "channels", "favorites", "watch_history", "epg_programs",
        "epg_channels", "filter_rules", "category_remap_rules")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ChannelDao::class, ChannelDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EpgDao::class, EpgDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FavoriteDao::class, FavoriteDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WatchHistoryDao::class, WatchHistoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FilterRuleDao::class, FilterRuleDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun channelDao(): ChannelDao = _channelDao.value

  public override fun epgDao(): EpgDao = _epgDao.value

  public override fun favoriteDao(): FavoriteDao = _favoriteDao.value

  public override fun watchHistoryDao(): WatchHistoryDao = _watchHistoryDao.value

  public override fun filterRuleDao(): FilterRuleDao = _filterRuleDao.value
}
