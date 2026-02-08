package com.aethertv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aethertv.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels ORDER BY name ASC")
    fun observeAll(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE categories LIKE '%' || :category || '%' ORDER BY name ASC")
    fun observeByCategory(category: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE infohash = :infohash")
    suspend fun getByInfohash(infohash: String): ChannelEntity?

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<ChannelEntity>>

    @Query("SELECT DISTINCT categories FROM channels")
    fun observeAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<ChannelEntity>)

    @Update
    suspend fun update(channel: ChannelEntity)

    @Query(
        """
        UPDATE channels SET
            isVerified = :isVerified,
            verifiedQuality = :quality,
            lastVerifiedAt = :verifiedAt,
            verifiedPeerCount = :peerCount
        WHERE infohash = :infohash
        """
    )
    suspend fun updateVerification(
        infohash: String,
        isVerified: Boolean,
        quality: String?,
        verifiedAt: Long,
        peerCount: Int?,
    )

    @Query("UPDATE channels SET epgChannelId = :epgChannelId WHERE infohash = :infohash")
    suspend fun updateEpgMapping(infohash: String, epgChannelId: String?)

    @Query("DELETE FROM channels WHERE lastScrapedAt < :before")
    suspend fun deleteStale(before: Long)

    @Query("DELETE FROM channels")
    suspend fun deleteAll()
}
