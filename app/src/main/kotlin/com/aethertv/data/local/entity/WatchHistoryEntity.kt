package com.aethertv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val infohash: String,
    val watchedAt: Long,
    val durationSeconds: Long,
)
