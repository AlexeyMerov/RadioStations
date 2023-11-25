package com.alexeymerov.radiostations.di

import android.content.Context
import com.alexeymerov.radiostations.database.RadioDatabase
import com.alexeymerov.radiostations.database.dao.CategoryDao
import com.alexeymerov.radiostations.database.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDB(@ApplicationContext context: Context): RadioDatabase = RadioDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCategoryDao(db: RadioDatabase): CategoryDao = db.categoryDao()

    @Singleton
    @Provides
    fun provideMediaDao(db: RadioDatabase): MediaDao = db.mediaDao()
}