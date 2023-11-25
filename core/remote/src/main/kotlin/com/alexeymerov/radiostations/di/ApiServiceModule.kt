package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.remote.api.RadioApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiServiceModule {

    @Provides
    @Singleton
    fun provideRadioApi(retrofit: Retrofit): RadioApi = retrofit.create(RadioApi::class.java)


}