package com.aethertv.domain.usecase

import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.remote.AceStreamEngineClient
import com.aethertv.data.repository.ChannelRepository
import javax.inject.Inject

class RefreshChannelsUseCase @Inject constructor(
    private val engineClient: AceStreamEngineClient,
    private val channelRepository: ChannelRepository,
    private val settingsDataStore: SettingsDataStore,
) {
    suspend operator fun invoke(): Result<Int> {
        return try {
            engineClient.waitForConnection()
            val channels = engineClient.searchAll()
            val now = System.currentTimeMillis()
            channelRepository.insertFromScraper(channels, now)
            settingsDataStore.setLastScrapeTime(now)
            Result.success(channels.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
