package com.aethertv.di

import com.aethertv.data.repository.ChannelRepository
import com.aethertv.data.repository.ChannelRepositoryImpl
import com.aethertv.data.repository.EpgRepository
import com.aethertv.data.repository.EpgRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChannelRepository(impl: ChannelRepositoryImpl): ChannelRepository

    @Binds
    @Singleton
    abstract fun bindEpgRepository(impl: EpgRepositoryImpl): EpgRepository
}
