package com.aethertv.domain.usecase

import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.repository.EpgRepository
import com.aethertv.domain.model.EpgProgram
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEpgUseCase @Inject constructor(
    private val epgRepository: EpgRepository,
) {
    fun forChannel(channelId: String, afterTime: Long): Flow<List<EpgProgram>> =
        epgRepository.observePrograms(channelId, afterTime)

    fun observeProgramsInRange(startTime: Long, endTime: Long): Flow<List<EpgProgram>> =
        epgRepository.observeProgramsInRange(startTime, endTime)

    fun observeAllEpgChannels(): Flow<List<EpgChannelEntity>> =
        epgRepository.observeAllEpgChannels()

    suspend fun currentProgram(channelId: String): EpgProgram? =
        epgRepository.getCurrentProgram(channelId, System.currentTimeMillis())
}
