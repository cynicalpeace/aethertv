package com.aethertv.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.aethertv.`data`.local.entity.WatchHistoryEntity
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
public class WatchHistoryDao_Impl(
  __db: RoomDatabase,
) : WatchHistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWatchHistoryEntity: EntityInsertAdapter<WatchHistoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWatchHistoryEntity = object : EntityInsertAdapter<WatchHistoryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `watch_history` (`id`,`infohash`,`watchedAt`,`durationSeconds`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WatchHistoryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.infohash)
        statement.bindLong(3, entity.watchedAt)
        statement.bindLong(4, entity.durationSeconds)
      }
    }
  }

  public override suspend fun insert(entry: WatchHistoryEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfWatchHistoryEntity.insert(_connection, entry)
  }

  public override fun observeRecent(limit: Int): Flow<List<WatchHistoryEntity>> {
    val _sql: String = "SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("watch_history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfInfohash: Int = getColumnIndexOrThrow(_stmt, "infohash")
        val _columnIndexOfWatchedAt: Int = getColumnIndexOrThrow(_stmt, "watchedAt")
        val _columnIndexOfDurationSeconds: Int = getColumnIndexOrThrow(_stmt, "durationSeconds")
        val _result: MutableList<WatchHistoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WatchHistoryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpInfohash: String
          _tmpInfohash = _stmt.getText(_columnIndexOfInfohash)
          val _tmpWatchedAt: Long
          _tmpWatchedAt = _stmt.getLong(_columnIndexOfWatchedAt)
          val _tmpDurationSeconds: Long
          _tmpDurationSeconds = _stmt.getLong(_columnIndexOfDurationSeconds)
          _item = WatchHistoryEntity(_tmpId,_tmpInfohash,_tmpWatchedAt,_tmpDurationSeconds)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentInfohashes(limit: Int): List<String> {
    val _sql: String = """
        |
        |        SELECT DISTINCT infohash FROM watch_history
        |        ORDER BY watchedAt DESC
        |        LIMIT ?
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
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

  public override suspend fun deleteOlderThan(before: Long) {
    val _sql: String = "DELETE FROM watch_history WHERE watchedAt < ?"
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
    val _sql: String = "DELETE FROM watch_history"
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
