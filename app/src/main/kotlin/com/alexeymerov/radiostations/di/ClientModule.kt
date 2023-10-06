package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.data.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClientImpl
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