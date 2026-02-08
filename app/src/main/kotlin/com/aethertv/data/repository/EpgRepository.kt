package com.aethertv.data.repository

import com.aethertv.data.local.EpgDao
import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.local.entity.EpgProgramEntity
import com.aethertv.domain.model.EpgProgram
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface EpgRepository {
    fun observePrograms(channelId: String, afterTime: Long): Flow<List<EpgProgram>>
    fun observeProgramsInRange(startTime: Long, endTime: Long): Flow<List<EpgProgram>>
    fun observeAllEpgChannels(): Flow<List<EpgChannelEntity>>
    suspend fun getCurrentProgram(channelId: String, time: Long): EpgProgram?
    suspend fun insertChannels(channels: List<EpgChannelEntity>)
    suspend fun insertPrograms(programs: List<EpgProgramEntity>)
    suspend fun deleteExpiredPrograms(before: Long)
    suspend fun clearAll()
}

@Singleton
class EpgRepositoryImpl @Inject constructor(
    private val epgDao: EpgDao,
) : EpgRepository {

    override fun observePrograms(channelId: String, afterTime: Long): Flow<List<EpgProgram>> {
        return epgDao.observePrograms(channelId, afterTime).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeProgramsInRange(startTime: Long, endTime: Long): Flow<List<EpgProgram>> {
        return epgDao.observeProgramsInRange(startTime, endTime).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeAllEpgChannels(): Flow<List<EpgChannelEntity>> {
        return epgDao.observeAllChannels()
    }

    override suspend fun getCurrentProgram(channelId: String, time: Long): EpgProgram? {
        return epgDao.getCurrentProgram(channelId, time)?.toDomain()
    }

    override suspend fun insertChannels(channels: List<EpgChannelEntity>) {
        epgDao.insertChannels(channels)
    }

    override suspend fun insertPrograms(programs: List<EpgProgramEntity>) {
        epgDao.insertPrograms(programs)
    }

    override suspend fun deleteExpiredPrograms(before: Long) {
        epgDao.deleteExpiredPrograms(before)
    }

    override suspend fun clearAll() {
        epgDao.deleteAllPrograms()
        epgDao.deleteAllChannels()
    }

    private fun EpgProgramEntity.toDomain(): EpgProgram {
        return EpgProgram(
            id = id,
            channelId = channelId,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            category = category,
            iconUrl = iconUrl,
        )
    }
}
