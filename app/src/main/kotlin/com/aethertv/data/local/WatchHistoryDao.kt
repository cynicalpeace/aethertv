package com.aethertv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aethertv.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<WatchHistoryEntity>>

    @Query(
        """
        SELECT DISTINCT infohash FROM watch_history
        ORDER BY watchedAt DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentInfohashes(limit: Int = 20): List<String>

    @Insert
    suspend fun insert(entry: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE watchedAt < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM watch_history")
    suspend fun deleteAll()
}
