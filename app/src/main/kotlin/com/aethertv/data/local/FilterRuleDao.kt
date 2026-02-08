package com.aethertv.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aethertv.data.local.entity.FilterRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterRuleDao {

    @Query("SELECT * FROM filter_rules ORDER BY type, id")
    fun observeAll(): Flow<List<FilterRuleEntity>>

    @Query("SELECT * FROM filter_rules WHERE isEnabled = 1")
    suspend fun getEnabled(): List<FilterRuleEntity>

    @Query("SELECT * FROM filter_rules WHERE type = :type")
    fun observeByType(type: String): Flow<List<FilterRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: FilterRuleEntity): Long

    @Update
    suspend fun update(rule: FilterRuleEntity)

    @Delete
    suspend fun delete(rule: FilterRuleEntity)

    @Query("DELETE FROM filter_rules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE filter_rules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)
}
