package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.data.repository.audio.MediaRepositoryImpl
import com.alexeymerov.radiostations.data.repository.category.CategoryRepository
import com.alexeymerov.radiostations.data.repository.category.CategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(repository: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(repository: MediaRepositoryImpl): MediaRepository

}