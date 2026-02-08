package com.aethertv.domain.usecase

import com.aethertv.data.repository.ChannelRepository
import com.aethertv.domain.model.Channel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
) {
    operator fun invoke(query: String): Flow<List<Channel>> =
        channelRepository.search(query)
}
