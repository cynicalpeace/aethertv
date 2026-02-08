package com.aethertv.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.aethertv.`data`.local.entity.EpgChannelEntity
import com.aethertv.`data`.local.entity.EpgProgramEntity
import javax.`annotation`.processing.Generated
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
public class EpgDao_Impl(
  __db: RoomDatabase,
) : EpgDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEpgChannelEntity: EntityInsertAdapter<EpgChannelEntity>

  private val __insertAdapterOfEpgProgramEntity: EntityInsertAdapter<EpgProgramEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfEpgChannelEntity = object : EntityInsertAdapter<EpgChannelEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `epg_channels` (`xmltvId`,`displayName`,`iconUrl`,`language`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EpgChannelEntity) {
        statement.bindText(1, entity.xmltvId)
        statement.bindText(2, entity.displayName)
        val _tmpIconUrl: String? = entity.iconUrl
        if (_tmpIconUrl == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpIconUrl)
        }
        val _tmpLanguage: String? = entity.language
        if (_tmpLanguage == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLanguage)
        }
      }
    }
    this.__insertAdapterOfEpgProgramEntity = object : EntityInsertAdapter<EpgProgramEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `epg_programs` (`id`,`channelId`,`title`,`description`,`startTime`,`endTime`,`category`,`iconUrl`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EpgProgramEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.channelId)
        statement.bindText(3, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDescription)
        }
        statement.bindLong(5, entity.startTime)
        statement.bindLong(6, entity.endTime)
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCategory)
        }
        val _tmpIconUrl: String? = entity.iconUrl
        if (_tmpIconUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpIconUrl)
        }
      }
    }
  }

  public override suspend fun insertChannels(channels: List<EpgChannelEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEpgChannelEntity.insert(_connection, channels)
  }

  public override suspend fun insertPrograms(programs: List<EpgProgramEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEpgProgramEntity.insert(_connection, programs)
  }

  public override fun observePrograms(channelId: String, afterTime: Long):
      Flow<List<EpgProgramEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM epg_programs
        |        WHERE channelId = ? AND endTime > ?
        |        ORDER BY startTime ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("epg_programs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, channelId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, afterTime)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfChannelId: Int = getColumnIndexOrThrow(_stmt, "channelId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _result: MutableList<EpgProgramEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EpgProgramEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpChannelId: String
          _tmpChannelId = _stmt.getText(_columnIndexOfChannelId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          _item =
              EpgProgramEntity(_tmpId,_tmpChannelId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpCategory,_tmpIconUrl)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCurrentProgram(channelId: String, time: Long): EpgProgramEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM epg_programs
        |        WHERE channelId = ? AND startTime <= ? AND endTime > ?
        |        LIMIT 1
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, channelId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, time)
        _argIndex = 3
        _stmt.bindLong(_argIndex, time)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfChannelId: Int = getColumnIndexOrThrow(_stmt, "channelId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _result: EpgProgramEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpChannelId: String
          _tmpChannelId = _stmt.getText(_columnIndexOfChannelId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          _result =
              EpgProgramEntity(_tmpId,_tmpChannelId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpCategory,_tmpIconUrl)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeProgramsInRange(startTime: Long, endTime: Long):
      Flow<List<EpgProgramEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM epg_programs
        |        WHERE startTime >= ? AND endTime <= ?
        |        ORDER BY channelId, startTime ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("epg_programs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endTime)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfChannelId: Int = getColumnIndexOrThrow(_stmt, "channelId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _result: MutableList<EpgProgramEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EpgProgramEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpChannelId: String
          _tmpChannelId = _stmt.getText(_columnIndexOfChannelId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          _item =
              EpgProgramEntity(_tmpId,_tmpChannelId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpCategory,_tmpIconUrl)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllChannels(): Flow<List<EpgChannelEntity>> {
    val _sql: String = "SELECT * FROM epg_channels ORDER BY displayName ASC"
    return createFlow(__db, false, arrayOf("epg_channels")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfXmltvId: Int = getColumnIndexOrThrow(_stmt, "xmltvId")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _result: MutableList<EpgChannelEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EpgChannelEntity
          val _tmpXmltvId: String
          _tmpXmltvId = _stmt.getText(_columnIndexOfXmltvId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          _item = EpgChannelEntity(_tmpXmltvId,_tmpDisplayName,_tmpIconUrl,_tmpLanguage)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getChannel(xmltvId: String): EpgChannelEntity? {
    val _sql: String = "SELECT * FROM epg_channels WHERE xmltvId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, xmltvId)
        val _columnIndexOfXmltvId: Int = getColumnIndexOrThrow(_stmt, "xmltvId")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfIconUrl: Int = getColumnIndexOrThrow(_stmt, "iconUrl")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _result: EpgChannelEntity?
        if (_stmt.step()) {
          val _tmpXmltvId: String
          _tmpXmltvId = _stmt.getText(_columnIndexOfXmltvId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpIconUrl: String?
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl)
          }
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          _result = EpgChannelEntity(_tmpXmltvId,_tmpDisplayName,_tmpIconUrl,_tmpLanguage)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteExpiredPrograms(before: Long) {
    val _sql: String = "DELETE FROM epg_programs WHERE endTime < ?"
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

  public override suspend fun deleteAllPrograms() {
    val _sql: String = "DELETE FROM epg_programs"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllChannels() {
    val _sql: String = "DELETE FROM epg_channels"
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
