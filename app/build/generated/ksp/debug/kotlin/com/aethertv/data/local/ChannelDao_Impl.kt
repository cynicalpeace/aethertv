package com.aethertv.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.aethertv.`data`.local.entity.ChannelEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ChannelDao_Impl(
  __db: RoomDatabase,
) : ChannelDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfChannelEntity: EntityInsertAdapter<ChannelEntity>

  private val __updateAdapterOfChannelEntity: EntityDeleteOrUpdateAdapter<ChannelEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfChannelEntity = object : EntityInsertAdapter<ChannelEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `channels` (`infohash`,`name`,`categories`,`languages`,`countries`,`iconUrl`,`status`,`availability`,`lastScrapedAt`,`isVerified`,`verifiedQuality`,`lastVerifiedAt`,`verifiedPeerCount`,`epgChannelId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChannelEntity) {
        statement.bindText(1, entity.infohash)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.categories)
        statement.bindText(4, entity.languages)
        statement.bindText(5, entity.countries)
        val _tmpIconUrl: String? = entity.iconUrl
        if (_tmpIconUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpIconUrl)
        }
        statement.bindLong(7, entity.status.toLong())
        statement.bindDouble(8, entity.availability.toDouble())
        statement.bindLong(9, entity.lastScrapedAt)
        val _tmpIsVerified: Boolean? = entity.isVerified
        val _tmp: Int? = _tmpIsVerified?.let { if (it) 1 else 0 }
        if (_tmp == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmp.toLong())
        }
        val _tmpVerifiedQuality: String? = entity.verifiedQuality
        if (_tmpVerifiedQuality == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpVerifiedQuality)
        }
        val _tmpLastVerifiedAt: Long? = entity.lastVerifiedAt
        if (_tmpLastVerifiedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLastVerifiedAt)
        }
        val _tmpVerifiedPeerCount: Int? = entity.verifiedPeerCount
        if (_tmpVerifiedPeerCount == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpVerifiedPeerCount.toLong())
        }
        val _tmpEpgChannelId: String? = entity.epgChannelId
        if (_tmpEpgChannelId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpEpgChannelId)
        }
      }
    }
    this.__updateAdapterOfChannelEntity = object : EntityDeleteOrUpdateAdapter<ChannelEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `channels` SET `infohash` = ?,`name` = ?,`categories` = ?,`languages` = ?,`countries` = ?,`iconUrl` = ?,`status` = ?,`availability` = ?,`lastScrapedAt` = ?,`isVerified` = ?,`verifiedQuality` = ?,`lastVerifiedAt` = ?,`verifiedPeerCount` = ?,`epgChannelId` = ? WHERE `infohash` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ChannelEntity) {
        statement.bindText(1, entity.infohash)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.categories)
        statement.bindText(4, entity.languages)
        statement.bindText(5, entity.countries)
        val _tmpIconUrl: String? = entity.iconUrl
        if (_tmpIconUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpIconUrl)
        }
        statement.bindLong(7, entity.status.toLong())
        statement.bindDouble(8, entity.availability.toDouble())
        statement.bindLong(9, entity.lastScrapedAt)
        val _tmpIsVerified: Boolean? = entity.isVerified
        val _tmp: Int? = _tmpIsVerified?.let { if (it) 1 else 0 }
        if (_tmp == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmp.toLong())
        }
        val _tmpVerifiedQuality: String? = entity.verifiedQuality
        if (_tmpVerifiedQuality == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpVerifiedQuality)
        }
        val _tmpLastVerifiedAt: Long? = entity.lastVerifiedAt
        if (_tmpLastVerifiedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLastVerifiedAt)
        }
        val _tmpVerifiedPeerCount: Int? = entity.verifiedPeerCount
        if (_tmpVerifiedPeerCount == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpVerifiedPeerCount.toLong())
        }
        val _tmpEpgChannelId: String? = entity.epgChannelId
        if (_tmpEpgChannelId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpEpgChannelId)
        }
        statement.bindText(15, entity.infohash)
      }
    }
  }

  public override suspend fun insertAll(channels: List<ChannelEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfChannelEntity.insert(_connection, channels)
  }

  public override suspend fun update(channel: ChannelEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfChannelEntity.handle(_connection, channel)
  }

  public override fun observeAll(): Flow<List<ChannelEntity>> {
    val _sql: String = "SELECT * FROM channels ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("channels")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInfohash: Int = getColumnIndexOrThrow(_stmt, "infohash")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfLanguages: Int = getColumnIndexOrThrow(_stmt, "languages")
        val _columnIndexOfCountries: Int = getColumnIndexOrThrow(_stmt, "countries")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAvailability: Int = getColumnIndexOrThrow(_stmt, "availability")
        val _columnIndexOfLastScrapedAt: Int = getColumnIndexOrThrow(_stmt, "lastScrapedAt")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedQuality: Int = getColumnIndexOrThrow(_stmt, "verifiedQuality")
        val _columnIndexOfLastVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastVerifiedAt")
        val _columnIndexOfVerifiedPeerCount: Int = getColumnIndexOrThrow(_stmt, "verifiedPeerCount")
        val _columnIndexOfEpgChannelId: Int = getColumnIndexOrThrow(_stmt, "epgChannelId")
        val _result: MutableList<ChannelEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChannelEntity
          val _tmpInfohash: String
          _tmpInfohash = _stmt.getText(_columnIndexOfInfohash)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpLanguages: String
          _tmpLanguages = _stmt.getText(_columnIndexOfLanguages)
          val _tmpCountries: String
          _tmpCountries = _stmt.getText(_columnIndexOfCountries)
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpAvailability: Float
          _tmpAvailability = _stmt.getDouble(_columnIndexOfAvailability).toFloat()
          val _tmpLastScrapedAt: Long
          _tmpLastScrapedAt = _stmt.getLong(_columnIndexOfLastScrapedAt)
          val _tmpIsVerified: Boolean?
          val _tmp: Int?
          if (_stmt.isNull(_columnIndexOfIsVerified)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          }
          _tmpIsVerified = _tmp?.let { it != 0 }
          val _tmpVerifiedQuality: String?
          if (_stmt.isNull(_columnIndexOfVerifiedQuality)) {
            _tmpVerifiedQuality = null
          } else {
            _tmpVerifiedQuality = _stmt.getText(_columnIndexOfVerifiedQuality)
          }
          val _tmpLastVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastVerifiedAt)) {
            _tmpLastVerifiedAt = null
          } else {
            _tmpLastVerifiedAt = _stmt.getLong(_columnIndexOfLastVerifiedAt)
          }
          val _tmpVerifiedPeerCount: Int?
          if (_stmt.isNull(_columnIndexOfVerifiedPeerCount)) {
            _tmpVerifiedPeerCount = null
          } else {
            _tmpVerifiedPeerCount = _stmt.getLong(_columnIndexOfVerifiedPeerCount).toInt()
          }
          val _tmpEpgChannelId: String?
          if (_stmt.isNull(_columnIndexOfEpgChannelId)) {
            _tmpEpgChannelId = null
          } else {
            _tmpEpgChannelId = _stmt.getText(_columnIndexOfEpgChannelId)
          }
          _item =
              ChannelEntity(_tmpInfohash,_tmpName,_tmpCategories,_tmpLanguages,_tmpCountries,_tmpIconUrl,_tmpStatus,_tmpAvailability,_tmpLastScrapedAt,_tmpIsVerified,_tmpVerifiedQuality,_tmpLastVerifiedAt,_tmpVerifiedPeerCount,_tmpEpgChannelId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByCategory(category: String): Flow<List<ChannelEntity>> {
    val _sql: String =
        "SELECT * FROM channels WHERE categories LIKE '%' || ? || '%' ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("channels")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfInfohash: Int = getColumnIndexOrThrow(_stmt, "infohash")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfLanguages: Int = getColumnIndexOrThrow(_stmt, "languages")
        val _columnIndexOfCountries: Int = getColumnIndexOrThrow(_stmt, "countries")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAvailability: Int = getColumnIndexOrThrow(_stmt, "availability")
        val _columnIndexOfLastScrapedAt: Int = getColumnIndexOrThrow(_stmt, "lastScrapedAt")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedQuality: Int = getColumnIndexOrThrow(_stmt, "verifiedQuality")
        val _columnIndexOfLastVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastVerifiedAt")
        val _columnIndexOfVerifiedPeerCount: Int = getColumnIndexOrThrow(_stmt, "verifiedPeerCount")
        val _columnIndexOfEpgChannelId: Int = getColumnIndexOrThrow(_stmt, "epgChannelId")
        val _result: MutableList<ChannelEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChannelEntity
          val _tmpInfohash: String
          _tmpInfohash = _stmt.getText(_columnIndexOfInfohash)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpLanguages: String
          _tmpLanguages = _stmt.getText(_columnIndexOfLanguages)
          val _tmpCountries: String
          _tmpCountries = _stmt.getText(_columnIndexOfCountries)
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpAvailability: Float
          _tmpAvailability = _stmt.getDouble(_columnIndexOfAvailability).toFloat()
          val _tmpLastScrapedAt: Long
          _tmpLastScrapedAt = _stmt.getLong(_columnIndexOfLastScrapedAt)
          val _tmpIsVerified: Boolean?
          val _tmp: Int?
          if (_stmt.isNull(_columnIndexOfIsVerified)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          }
          _tmpIsVerified = _tmp?.let { it != 0 }
          val _tmpVerifiedQuality: String?
          if (_stmt.isNull(_columnIndexOfVerifiedQuality)) {
            _tmpVerifiedQuality = null
          } else {
            _tmpVerifiedQuality = _stmt.getText(_columnIndexOfVerifiedQuality)
          }
          val _tmpLastVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastVerifiedAt)) {
            _tmpLastVerifiedAt = null
          } else {
            _tmpLastVerifiedAt = _stmt.getLong(_columnIndexOfLastVerifiedAt)
          }
          val _tmpVerifiedPeerCount: Int?
          if (_stmt.isNull(_columnIndexOfVerifiedPeerCount)) {
            _tmpVerifiedPeerCount = null
          } else {
            _tmpVerifiedPeerCount = _stmt.getLong(_columnIndexOfVerifiedPeerCount).toInt()
          }
          val _tmpEpgChannelId: String?
          if (_stmt.isNull(_columnIndexOfEpgChannelId)) {
            _tmpEpgChannelId = null
          } else {
            _tmpEpgChannelId = _stmt.getText(_columnIndexOfEpgChannelId)
          }
          _item =
              ChannelEntity(_tmpInfohash,_tmpName,_tmpCategories,_tmpLanguages,_tmpCountries,_tmpIconUrl,_tmpStatus,_tmpAvailability,_tmpLastScrapedAt,_tmpIsVerified,_tmpVerifiedQuality,_tmpLastVerifiedAt,_tmpVerifiedPeerCount,_tmpEpgChannelId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByInfohash(infohash: String): ChannelEntity? {
    val _sql: String = "SELECT * FROM channels WHERE infohash = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, infohash)
        val _columnIndexOfInfohash: Int = getColumnIndexOrThrow(_stmt, "infohash")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfLanguages: Int = getColumnIndexOrThrow(_stmt, "languages")
        val _columnIndexOfCountries: Int = getColumnIndexOrThrow(_stmt, "countries")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAvailability: Int = getColumnIndexOrThrow(_stmt, "availability")
        val _columnIndexOfLastScrapedAt: Int = getColumnIndexOrThrow(_stmt, "lastScrapedAt")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedQuality: Int = getColumnIndexOrThrow(_stmt, "verifiedQuality")
        val _columnIndexOfLastVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastVerifiedAt")
        val _columnIndexOfVerifiedPeerCount: Int = getColumnIndexOrThrow(_stmt, "verifiedPeerCount")
        val _columnIndexOfEpgChannelId: Int = getColumnIndexOrThrow(_stmt, "epgChannelId")
        val _result: ChannelEntity?
        if (_stmt.step()) {
          val _tmpInfohash: String
          _tmpInfohash = _stmt.getText(_columnIndexOfInfohash)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpLanguages: String
          _tmpLanguages = _stmt.getText(_columnIndexOfLanguages)
          val _tmpCountries: String
          _tmpCountries = _stmt.getText(_columnIndexOfCountries)
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpAvailability: Float
          _tmpAvailability = _stmt.getDouble(_columnIndexOfAvailability).toFloat()
          val _tmpLastScrapedAt: Long
          _tmpLastScrapedAt = _stmt.getLong(_columnIndexOfLastScrapedAt)
          val _tmpIsVerified: Boolean?
          val _tmp: Int?
          if (_stmt.isNull(_columnIndexOfIsVerified)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          }
          _tmpIsVerified = _tmp?.let { it != 0 }
          val _tmpVerifiedQuality: String?
          if (_stmt.isNull(_columnIndexOfVerifiedQuality)) {
            _tmpVerifiedQuality = null
          } else {
            _tmpVerifiedQuality = _stmt.getText(_columnIndexOfVerifiedQuality)
          }
          val _tmpLastVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastVerifiedAt)) {
            _tmpLastVerifiedAt = null
          } else {
            _tmpLastVerifiedAt = _stmt.getLong(_columnIndexOfLastVerifiedAt)
          }
          val _tmpVerifiedPeerCount: Int?
          if (_stmt.isNull(_columnIndexOfVerifiedPeerCount)) {
            _tmpVerifiedPeerCount = null
          } else {
            _tmpVerifiedPeerCount = _stmt.getLong(_columnIndexOfVerifiedPeerCount).toInt()
          }
          val _tmpEpgChannelId: String?
          if (_stmt.isNull(_columnIndexOfEpgChannelId)) {
            _tmpEpgChannelId = null
          } else {
            _tmpEpgChannelId = _stmt.getText(_columnIndexOfEpgChannelId)
          }
          _result =
              ChannelEntity(_tmpInfohash,_tmpName,_tmpCategories,_tmpLanguages,_tmpCountries,_tmpIconUrl,_tmpStatus,_tmpAvailability,_tmpLastScrapedAt,_tmpIsVerified,_tmpVerifiedQuality,_tmpLastVerifiedAt,_tmpVerifiedPeerCount,_tmpEpgChannelId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun search(query: String): Flow<List<ChannelEntity>> {
    val _sql: String = "SELECT * FROM channels WHERE name LIKE '%' || ? || '%' ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("channels")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfInfohash: Int = getColumnIndexOrThrow(_stmt, "infohash")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategories: Int = getColumnIndexOrThrow(_stmt, "categories")
        val _columnIndexOfLanguages: Int = getColumnIndexOrThrow(_stmt, "languages")
        val _columnIndexOfCountries: Int = getColumnIndexOrThrow(_stmt, "countries")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAvailability: Int = getColumnIndexOrThrow(_stmt, "availability")
        val _columnIndexOfLastScrapedAt: Int = getColumnIndexOrThrow(_stmt, "lastScrapedAt")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedQuality: Int = getColumnIndexOrThrow(_stmt, "verifiedQuality")
        val _columnIndexOfLastVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastVerifiedAt")
        val _columnIndexOfVerifiedPeerCount: Int = getColumnIndexOrThrow(_stmt, "verifiedPeerCount")
        val _columnIndexOfEpgChannelId: Int = getColumnIndexOrThrow(_stmt, "epgChannelId")
        val _result: MutableList<ChannelEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChannelEntity
          val _tmpInfohash: String
          _tmpInfohash = _stmt.getText(_columnIndexOfInfohash)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategories: String
          _tmpCategories = _stmt.getText(_columnIndexOfCategories)
          val _tmpLanguages: String
          _tmpLanguages = _stmt.getText(_columnIndexOfLanguages)
          val _tmpCountries: String
          _tmpCountries = _stmt.getText(_columnIndexOfCountries)
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpAvailability: Float
          _tmpAvailability = _stmt.getDouble(_columnIndexOfAvailability).toFloat()
          val _tmpLastScrapedAt: Long
          _tmpLastScrapedAt = _stmt.getLong(_columnIndexOfLastScrapedAt)
          val _tmpIsVerified: Boolean?
          val _tmp: Int?
          if (_stmt.isNull(_columnIndexOfIsVerified)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          }
          _tmpIsVerified = _tmp?.let { it != 0 }
          val _tmpVerifiedQuality: String?
          if (_stmt.isNull(_columnIndexOfVerifiedQuality)) {
            _tmpVerifiedQuality = null
          } else {
            _tmpVerifiedQuality = _stmt.getText(_columnIndexOfVerifiedQuality)
          }
          val _tmpLastVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastVerifiedAt)) {
            _tmpLastVerifiedAt = null
          } else {
            _tmpLastVerifiedAt = _stmt.getLong(_columnIndexOfLastVerifiedAt)
          }
          val _tmpVerifiedPeerCount: Int?
          if (_stmt.isNull(_columnIndexOfVerifiedPeerCount)) {
            _tmpVerifiedPeerCount = null
          } else {
            _tmpVerifiedPeerCount = _stmt.getLong(_columnIndexOfVerifiedPeerCount).toInt()
          }
          val _tmpEpgChannelId: String?
          if (_stmt.isNull(_columnIndexOfEpgChannelId)) {
            _tmpEpgChannelId = null
          } else {
            _tmpEpgChannelId = _stmt.getText(_columnIndexOfEpgChannelId)
          }
          _item =
              ChannelEntity(_tmpInfohash,_tmpName,_tmpCategories,_tmpLanguages,_tmpCountries,_tmpIconUrl,_tmpStatus,_tmpAvailability,_tmpLastScrapedAt,_tmpIsVerified,_tmpVerifiedQuality,_tmpLastVerifiedAt,_tmpVerifiedPeerCount,_tmpEpgChannelId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllCategories(): Flow<List<String>> {
    val _sql: String = "SELECT DISTINCT categories FROM channels"
    return createFlow(__db, false, arrayOf("channels")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateVerification(
    infohash: String,
    isVerified: Boolean,
    quality: String?,
    verifiedAt: Long,
    peerCount: Int?,
  ) {
    val _sql: String = """
        |
        |        UPDATE channels SET
        |            isVerified = ?,
        |            verifiedQuality = ?,
        |            lastVerifiedAt = ?,
        |            verifiedPeerCount = ?
        |        WHERE infohash = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isVerified) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        if (quality == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, quality)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, verifiedAt)
        _argIndex = 4
        if (peerCount == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, peerCount.toLong())
        }
        _argIndex = 5
        _stmt.bindText(_argIndex, infohash)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateEpgMapping(infohash: String, epgChannelId: String?) {
    val _sql: String = "UPDATE channels SET epgChannelId = ? WHERE infohash = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (epgChannelId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, epgChannelId)
        }
        _argIndex = 2
        _stmt.bindText(_argIndex, infohash)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteStale(before: Long) {
    val _sql: String = "DELETE FROM channels WHERE lastScrapedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, before)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM channels"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
