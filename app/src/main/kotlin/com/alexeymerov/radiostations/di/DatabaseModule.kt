package com.alexeymerov.radiostations.di

import android.content.Context
import com.alexeymerov.radiostations.data.local.db.RadioDatabase
import com.alexeymerov.radiostations.data.local.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.local.db.dao.MediaDao
import com.alexeymerov.radiostations.data.mapper.category.CategoryMapper
import com.alexeymerov.radiostations.data.mapper.category.CategoryMapperImpl
import com.alexeymerov.radiostations.data.mapper.media.MediaMapper
import com.alexeymerov.radiostations.data.mapper.media.MediaMapperImp
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
    fun provideMediaDao(db: RadioDatabase): MediaDao = db.mediaDao()

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Mapper {

        @Binds
        @Singleton
        abstract fun bindCategoryMapper(mapper: CategoryMapperImpl): CategoryMapper

        @Binds
        @Singleton
        abstract fun bindMediaMapper(mapper: MediaMapperImp): MediaMapper
    }

}