package com.aethertv.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.aethertv.`data`.local.entity.FavoriteEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class FavoriteDao_Impl(
  __db: RoomDatabase,
) : FavoriteDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteEntity: EntityInsertAdapter<FavoriteEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteEntity = object : EntityInsertAdapter<FavoriteEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `favorites` (`infohash`,`addedAt`,`sortOrder`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteEntity) {
        statement.bindText(1, entity.infohash)
        statement.bindLong(2, entity.addedAt)
        statement.bindLong(3, entity.sortOrder.toLong())
      }
    }
  }

  public override suspend fun insert(favorite: FavoriteEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFavoriteEntity.insert(_connection, favorite)
  }

  public override fun observeAll(): Flow<List<FavoriteEntity>> {
    val _sql: String = "SELECT * FROM favorites ORDER BY sortOrder ASC, addedAt DESC"
    return createFlow(__db, false, arrayOf("favorites")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInfohash: Int = getColumnIndexOrThrow(_stmt, "infohash")
        val _columnIndexOfAddedAt: Int = getColumnIndexOrThrow(_stmt, "addedAt")
        val _columnIndexOfSortOrder: Int = getColumnIndexOrThrow(_stmt, "sortOrder")
        val _result: MutableList<FavoriteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteEntity
          val _tmpInfohash: String
          _tmpInfohash = _stmt.getText(_columnIndexOfInfohash)
          val _tmpAddedAt: Long
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt)
          val _tmpSortOrder: Int
          _tmpSortOrder = _stmt.getLong(_columnIndexOfSortOrder).toInt()
          _item = FavoriteEntity(_tmpInfohash,_tmpAddedAt,_tmpSortOrder)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeIsFavorite(infohash: String): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorites WHERE infohash = ?)"
    return createFlow(__db, false, arrayOf("favorites")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, infohash)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isFavorite(infohash: String): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorites WHERE infohash = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, infohash)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllInfohashes(): List<String> {
    val _sql: String = "SELECT infohash FROM favorites ORDER BY sortOrder ASC, addedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
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

  public override suspend fun delete(infohash: String) {
    val _sql: String = "DELETE FROM favorites WHERE infohash = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, infohash)
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
