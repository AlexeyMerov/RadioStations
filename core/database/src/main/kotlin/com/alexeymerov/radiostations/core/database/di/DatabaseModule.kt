package com.alexeymerov.radiostations.core.database.di

import android.content.Context
import com.alexeymerov.radiostations.core.database.RadioDatabase
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.database.dao.CountryDao
import com.alexeymerov.radiostations.core.database.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDB(@ApplicationContext context: Context): RadioDatabase = RadioDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCategoryDao(db: RadioDatabase): CategoryDao = db.categoryDao()

    @Singleton
    @Provides
    fun provideMediaDao(db: RadioDatabase): MediaDao = db.mediaDao()

    @Singleton
    @Provides
    fun provideCountryDao(db: RadioDatabase): CountryDao = db.countryDao()
}