package com.aethertv.di

import android.content.Context
import androidx.room.Room
import com.aethertv.data.local.AetherTvDatabase
import com.aethertv.data.local.ChannelDao
import com.aethertv.data.local.EpgDao
import com.aethertv.data.local.FavoriteDao
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
        ).build()
    }

    @Provides
    fun provideChannelDao(db: AetherTvDatabase): ChannelDao = db.channelDao()

    @Provides
    fun provideEpgDao(db: AetherTvDatabase): EpgDao = db.epgDao()

    @Provides
    fun provideFavoriteDao(db: AetherTvDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideWatchHistoryDao(db: AetherTvDatabase): WatchHistoryDao = db.watchHistoryDao()
}
