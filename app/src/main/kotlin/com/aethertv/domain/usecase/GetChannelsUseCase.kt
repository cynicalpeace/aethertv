package com.aethertv.domain.usecase

import com.aethertv.data.repository.ChannelRepository
import com.aethertv.domain.model.Channel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
) {
    fun all(): Flow<List<Channel>> = channelRepository.observeAll()

    fun byCategory(category: String): Flow<List<Channel>> =
        channelRepository.observeByCategory(category)

    fun categories(): Flow<List<String>> = channelRepository.observeCategories()
}
