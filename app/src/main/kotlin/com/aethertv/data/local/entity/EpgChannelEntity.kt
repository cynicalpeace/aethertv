package com.aethertv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "epg_channels")
data class EpgChannelEntity(
    @PrimaryKey val xmltvId: String,
    val displayName: String,
    val iconUrl: String?,
    val language: String?,
)
