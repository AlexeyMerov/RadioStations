package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.data.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.data.mapper.response.ResponseMapperImpl
import com.alexeymerov.radiostations.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.data.repository.audio.MediaRepositoryImpl
import com.alexeymerov.radiostations.data.repository.category.CategoryRepository
import com.alexeymerov.radiostations.data.repository.category.CategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [RepositoryModule.Mapper::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(repository: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(repository: MediaRepositoryImpl): MediaRepository

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Mapper {

        @Binds
        @Singleton
        abstract fun bindResponseMapper(mapper: ResponseMapperImpl): ResponseMapper
    }

}