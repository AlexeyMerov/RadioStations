package com.example.radiostations.di


import com.example.radiostations.data.repository.CategoryRepository
import com.example.radiostations.data.repository.CategoryRepositoryImpl
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

}