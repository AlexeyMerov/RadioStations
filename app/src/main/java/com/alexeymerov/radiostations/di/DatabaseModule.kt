package com.alexeymerov.radiostations.di

import android.content.Context
import com.alexeymerov.radiostations.data.db.RadioDatabase
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.dao.StationDao
import com.alexeymerov.radiostations.data.mapper.EntityCategoryMapper
import com.alexeymerov.radiostations.data.mapper.EntityCategoryMapperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [DatabaseModule.Mapper::class])
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
    fun provideStationDao(db: RadioDatabase): StationDao = db.stationDao()

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Mapper {

        @Binds
        @Singleton
        abstract fun bindCategoryMapper(repository: EntityCategoryMapperImpl): EntityCategoryMapper
    }

}