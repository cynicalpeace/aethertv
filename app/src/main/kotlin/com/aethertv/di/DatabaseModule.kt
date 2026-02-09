package com.aethertv.di

import android.content.Context
import androidx.room.Room
import com.aethertv.data.local.AetherTvDatabase
import com.aethertv.data.local.ChannelDao
import com.aethertv.data.local.EpgDao
import com.aethertv.data.local.FavoriteDao
import com.aethertv.data.local.FilterRuleDao
import com.aethertv.data.local.WatchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AetherTvDatabase {
        return Room.databaseBuilder(
            context,
            AetherTvDatabase::class.java,
            "aethertv.db",
        )
            // For v1 -> v2 migrations, add migration objects here:
            // .addMigrations(MIGRATION_1_2)
            // For now, fallback to destructive migration to prevent crashes
            // on schema changes during development
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideChannelDao(db: AetherTvDatabase): ChannelDao = db.channelDao()

    @Provides
    fun provideEpgDao(db: AetherTvDatabase): EpgDao = db.epgDao()

    @Provides
    fun provideFavoriteDao(db: AetherTvDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideWatchHistoryDao(db: AetherTvDatabase): WatchHistoryDao = db.watchHistoryDao()

    @Provides
    fun provideFilterRuleDao(db: AetherTvDatabase): FilterRuleDao = db.filterRuleDao()
}
