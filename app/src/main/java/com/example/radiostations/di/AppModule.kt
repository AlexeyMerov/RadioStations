package com.example.radiostations.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_NAME, AppCompatActivity.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideAppResources(@ApplicationContext context: Context): Resources = context.resources

    private companion object {
        const val SHARED_NAME = "shared_radio"
    }
}