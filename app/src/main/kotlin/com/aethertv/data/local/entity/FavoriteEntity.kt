package com.aethertv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val infohash: String,
    val addedAt: Long,
    val sortOrder: Int = 0,
)
