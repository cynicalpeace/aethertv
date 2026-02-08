package com.aethertv.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aethertv.data.local.entity.CategoryRemapRuleEntity
import com.aethertv.data.local.entity.ChannelEntity
import com.aethertv.data.local.entity.EpgChannelEntity
import com.aethertv.data.local.entity.EpgProgramEntity
import com.aethertv.data.local.entity.FavoriteEntity
import com.aethertv.data.local.entity.FilterRuleEntity
import com.aethertv.data.local.entity.WatchHistoryEntity

@Database(
    entities = [
        ChannelEntity::class,
        FavoriteEntity::class,
        WatchHistoryEntity::class,
        EpgProgramEntity::class,
        EpgChannelEntity::class,
        FilterRuleEntity::class,
        CategoryRemapRuleEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AetherTvDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun epgDao(): EpgDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun filterRuleDao(): FilterRuleDao
}
