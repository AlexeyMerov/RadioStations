package com.alexeymerov.radiostations.core.remote.di


import com.alexeymerov.radiostations.core.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ClientModule {

    @Binds
    @Singleton
    abstract fun bindRadioClient(client: RadioClientImpl): RadioClient

}