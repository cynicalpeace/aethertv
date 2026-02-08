package com.aethertv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.local.entity.EpgProgramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpgDao {

    @Query(
        """
        SELECT * FROM epg_programs
        WHERE channelId = :channelId AND endTime > :afterTime
        ORDER BY startTime ASC
        """
    )
    fun observePrograms(channelId: String, afterTime: Long): Flow<List<EpgProgramEntity>>

    @Query(
        """
        SELECT * FROM epg_programs
        WHERE channelId = :channelId AND startTime <= :time AND endTime > :time
        LIMIT 1
        """
    )
    suspend fun getCurrentProgram(channelId: String, time: Long): EpgProgramEntity?

    @Query(
        """
        SELECT * FROM epg_programs
        WHERE startTime >= :startTime AND endTime <= :endTime
        ORDER BY channelId, startTime ASC
        """
    )
    fun observeProgramsInRange(startTime: Long, endTime: Long): Flow<List<EpgProgramEntity>>

    @Query("SELECT * FROM epg_channels ORDER BY displayName ASC")
    fun observeAllChannels(): Flow<List<EpgChannelEntity>>

    @Query("SELECT * FROM epg_channels WHERE xmltvId = :xmltvId")
    suspend fun getChannel(xmltvId: String): EpgChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<EpgChannelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<EpgProgramEntity>)

    @Query("DELETE FROM epg_programs WHERE endTime < :before")
    suspend fun deleteExpiredPrograms(before: Long)

    @Query("DELETE FROM epg_programs")
    suspend fun deleteAllPrograms()

    @Query("DELETE FROM epg_channels")
    suspend fun deleteAllChannels()
}
