package com.aethertv.data.repository

import com.aethertv.data.local.ChannelDao
import com.aethertv.data.local.FavoriteDao
import com.aethertv.data.local.entity.ChannelEntity
import com.aethertv.data.local.entity.FavoriteEntity
import com.aethertv.data.remote.AceStreamChannel
import com.aethertv.domain.model.Channel
import com.aethertv.domain.model.StreamQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

interface ChannelRepository {
    fun observeAll(): Flow<List<Channel>>
    fun observeByCategory(category: String): Flow<List<Channel>>
    fun observeCategories(): Flow<List<String>>
    fun search(query: String): Flow<List<Channel>>
    suspend fun getByInfohash(infohash: String): Channel?
    suspend fun insertFromScraper(channels: List<AceStreamChannel>, scrapedAt: Long)
    suspend fun updateVerification(
        infohash: String,
        isVerified: Boolean,
        quality: String?,
        verifiedAt: Long,
        peerCount: Int?,
    )
    suspend fun updateEpgMapping(infohash: String, epgChannelId: String?)
    suspend fun toggleFavorite(infohash: String)
    suspend fun deleteStale(before: Long)
}

@Singleton
class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao,
    private val favoriteDao: FavoriteDao,
    private val json: Json,
) : ChannelRepository {

    override fun observeAll(): Flow<List<Channel>> {
        return combine(
            channelDao.observeAll(),
            favoriteDao.observeAll(),
        ) { channels, favorites ->
            val favoriteSet = favorites.map { it.infohash }.toSet()
            channels.map { it.toDomain(favoriteSet) }
        }
    }

    override fun observeByCategory(category: String): Flow<List<Channel>> {
        return combine(
            channelDao.observeByCategory(category),
            favoriteDao.observeAll(),
        ) { channels, favorites ->
            val favoriteSet = favorites.map { it.infohash }.toSet()
            channels.map { it.toDomain(favoriteSet) }
        }
    }

    override fun observeCategories(): Flow<List<String>> {
        return channelDao.observeAllCategories().map { rawCategories ->
            rawCategories.flatMap { parseJsonArray(it) }.distinct().sorted()
        }
    }

    override fun search(query: String): Flow<List<Channel>> {
        return combine(
            channelDao.search(query),
            favoriteDao.observeAll(),
        ) { channels, favorites ->
            val favoriteSet = favorites.map { it.infohash }.toSet()
            channels.map { it.toDomain(favoriteSet) }
        }
    }

    override suspend fun getByInfohash(infohash: String): Channel? {
        val entity = channelDao.getByInfohash(infohash) ?: return null
        val isFav = favoriteDao.isFavorite(infohash)
        return entity.toDomain(if (isFav) setOf(infohash) else emptySet())
    }

    override suspend fun insertFromScraper(channels: List<AceStreamChannel>, scrapedAt: Long) {
        val entities = channels.map { ch ->
            ChannelEntity(
                infohash = ch.infohash,
                name = ch.name,
                categories = json.encodeToString(ch.categories),
                languages = json.encodeToString(ch.languages),
                countries = json.encodeToString(ch.countries),
                iconUrl = ch.iconUrl,
                status = ch.status,
                availability = ch.availability,
                lastScrapedAt = scrapedAt,
            )
        }
        channelDao.insertAll(entities)
    }

    override suspend fun updateVerification(
        infohash: String,
        isVerified: Boolean,
        quality: String?,
        verifiedAt: Long,
        peerCount: Int?,
    ) {
        channelDao.updateVerification(infohash, isVerified, quality, verifiedAt, peerCount)
    }

    override suspend fun updateEpgMapping(infohash: String, epgChannelId: String?) {
        channelDao.updateEpgMapping(infohash, epgChannelId)
    }

    override suspend fun toggleFavorite(infohash: String) {
        if (favoriteDao.isFavorite(infohash)) {
            favoriteDao.delete(infohash)
        } else {
            favoriteDao.insert(FavoriteEntity(infohash = infohash, addedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun deleteStale(before: Long) {
        channelDao.deleteStale(before)
    }

    private fun ChannelEntity.toDomain(favoriteSet: Set<String>): Channel {
        return Channel(
            infohash = infohash,
            name = name,
            categories = parseJsonArray(categories),
            languages = parseJsonArray(languages),
            countries = parseJsonArray(countries),
            iconUrl = iconUrl,
            status = status,
            availability = availability,
            lastScrapedAt = lastScrapedAt,
            isVerified = isVerified,
            verifiedQuality = verifiedQuality?.let { q ->
                StreamQuality.entries.firstOrNull { it.label == q }
            },
            lastVerifiedAt = lastVerifiedAt,
            verifiedPeerCount = verifiedPeerCount,
            epgChannelId = epgChannelId,
            isFavorite = infohash in favoriteSet,
        )
    }

    private fun parseJsonArray(jsonString: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }
}
