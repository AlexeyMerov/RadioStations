package com.alexeymerov.radiostations.di

import com.alexeymerov.radiostations.mediaservice.MediaServiceManager
import com.alexeymerov.radiostations.mediaservice.MediaServiceManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ActivityModule {

    @Singleton
    @Binds
    abstract fun bindMediaServiceManager(manager: MediaServiceManagerImpl): MediaServiceManager

}