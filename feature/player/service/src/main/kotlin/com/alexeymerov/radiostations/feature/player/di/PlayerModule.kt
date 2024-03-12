package com.alexeymerov.radiostations.feature.player.di

import com.alexeymerov.radiostations.feature.player.manager.MediaServiceManager
import com.alexeymerov.radiostations.feature.player.manager.MediaServiceManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Singleton
    @Binds
    abstract fun bindMediaServiceManager(manager: MediaServiceManagerImpl): MediaServiceManager

}