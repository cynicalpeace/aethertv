package com.aethertv.domain.usecase

import com.aethertv.data.local.FavoriteDao
import com.aethertv.data.local.WatchHistoryDao
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.data.repository.ChannelRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyStreamsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val favoriteDao: FavoriteDao,
    private val watchHistoryDao: WatchHistoryDao,
    private val settingsDataStore: SettingsDataStore,
) {
    suspend fun getInfohashesToVerify(): List<String> {
        val scope = settingsDataStore.verifyScope.first()
        return when (scope) {
            "favorites" -> favoriteDao.getAllInfohashes()
            "favorites_recent" -> {
                val favorites = favoriteDao.getAllInfohashes()
                val recent = watchHistoryDao.getRecentInfohashes()
                (favorites + recent).distinct()
            }
            "all" -> {
                channelRepository.observeAll().first().map { it.infohash }
            }
            else -> favoriteDao.getAllInfohashes()
        }
    }
}
