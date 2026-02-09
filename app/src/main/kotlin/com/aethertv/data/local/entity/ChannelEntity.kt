package com.aethertv.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "channels",
    indices = [Index("epgChannelId")]
)
data class ChannelEntity(
    @PrimaryKey val infohash: String,
    val name: String,
    val categories: String,       // JSON array
    val languages: String,        // JSON array
    val countries: String,        // JSON array
    val iconUrl: String?,
    val status: Int,
    val availability: Float,
    val lastScrapedAt: Long,
    // Verification fields
    val isVerified: Boolean? = null,
    val verifiedQuality: String? = null,  // "1080p", "720p", "480p"
    val lastVerifiedAt: Long? = null,
    val verifiedPeerCount: Int? = null,
    // EPG linkage
    val epgChannelId: String? = null,
)
