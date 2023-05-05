package com.example.radiostations.di

import android.content.Context
import com.example.radiostations.data.db.RadioDatabase
import com.example.radiostations.data.db.dao.CategoryDao
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

}