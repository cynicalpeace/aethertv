package com.aethertv.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.aethertv.`data`.local.entity.FilterRuleEntity
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
public class FilterRuleDao_Impl(
  __db: RoomDatabase,
) : FilterRuleDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFilterRuleEntity: EntityInsertAdapter<FilterRuleEntity>

  private val __deleteAdapterOfFilterRuleEntity: EntityDeleteOrUpdateAdapter<FilterRuleEntity>

  private val __updateAdapterOfFilterRuleEntity: EntityDeleteOrUpdateAdapter<FilterRuleEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFilterRuleEntity = object : EntityInsertAdapter<FilterRuleEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `filter_rules` (`id`,`type`,`pattern`,`isEnabled`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FilterRuleEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.type)
        statement.bindText(3, entity.pattern)
        val _tmp: Int = if (entity.isEnabled) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }
    this.__deleteAdapterOfFilterRuleEntity = object :
        EntityDeleteOrUpdateAdapter<FilterRuleEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `filter_rules` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FilterRuleEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfFilterRuleEntity = object :
        EntityDeleteOrUpdateAdapter<FilterRuleEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `filter_rules` SET `id` = ?,`type` = ?,`pattern` = ?,`isEnabled` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FilterRuleEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.type)
        statement.bindText(3, entity.pattern)
        val _tmp: Int = if (entity.isEnabled) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.id)
      }
    }
  }

  public override suspend fun insert(rule: FilterRuleEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfFilterRuleEntity.insertAndReturnId(_connection, rule)
    _result
  }

  public override suspend fun delete(rule: FilterRuleEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfFilterRuleEntity.handle(_connection, rule)
  }

  public override suspend fun update(rule: FilterRuleEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfFilterRuleEntity.handle(_connection, rule)
  }

  public override fun observeAll(): Flow<List<FilterRuleEntity>> {
    val _sql: String = "SELECT * FROM filter_rules ORDER BY type, id"
    return createFlow(__db, false, arrayOf("filter_rules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _result: MutableList<FilterRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FilterRuleEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          _item = FilterRuleEntity(_tmpId,_tmpType,_tmpPattern,_tmpIsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEnabled(): List<FilterRuleEntity> {
    val _sql: String = "SELECT * FROM filter_rules WHERE isEnabled = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _result: MutableList<FilterRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FilterRuleEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          _item = FilterRuleEntity(_tmpId,_tmpType,_tmpPattern,_tmpIsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByType(type: String): Flow<List<FilterRuleEntity>> {
    val _sql: String = "SELECT * FROM filter_rules WHERE type = ?"
    return createFlow(__db, false, arrayOf("filter_rules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _result: MutableList<FilterRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FilterRuleEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          _item = FilterRuleEntity(_tmpId,_tmpType,_tmpPattern,_tmpIsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM filter_rules WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setEnabled(id: Long, enabled: Boolean) {
    val _sql: String = "UPDATE filter_rules SET isEnabled = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (enabled) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
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
