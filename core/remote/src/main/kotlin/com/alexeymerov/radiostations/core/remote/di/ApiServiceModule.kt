package com.alexeymerov.radiostations.core.remote.di


import com.alexeymerov.radiostations.core.remote.api.RadioApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideRadioApi(@Backend(Server.Radio) retrofit: Retrofit): RadioApi = retrofit.create(RadioApi::class.java)

}