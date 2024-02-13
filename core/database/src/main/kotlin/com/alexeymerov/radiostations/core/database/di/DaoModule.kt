package com.alexeymerov.radiostations.core.database.di

import com.alexeymerov.radiostations.core.database.RadioDatabase
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.database.dao.CountryDao
import com.alexeymerov.radiostations.core.database.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

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