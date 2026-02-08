package com.aethertv.domain.model

data class Channel(
    val infohash: String,
    val name: String,
    val categories: List<String>,
    val languages: List<String>,
    val countries: List<String>,
    val iconUrl: String?,
    val status: Int,
    val availability: Float,
    val lastScrapedAt: Long,
    val isVerified: Boolean?,
    val verifiedQuality: StreamQuality?,
    val lastVerifiedAt: Long?,
    val verifiedPeerCount: Int?,
    val epgChannelId: String?,
    val isFavorite: Boolean = false,
)
