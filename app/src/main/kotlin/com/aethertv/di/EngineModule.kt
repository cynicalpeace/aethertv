package com.aethertv.di

import com.aethertv.engine.AceStreamEngine
import com.aethertv.engine.StreamEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EngineModule {

    /**
     * Bind the StreamEngine interface to AceStreamEngine implementation.
     * 
     * To switch to a different engine (e.g., LibTorrent-based),
     * create a new implementation and change this binding.
     */
    @Binds
    @Singleton
    abstract fun bindStreamEngine(aceStreamEngine: AceStreamEngine): StreamEngine
}
