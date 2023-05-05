package com.example.radiostations.di


import com.example.radiostations.domain.usecase.category.CategoryUseCase
import com.example.radiostations.domain.usecase.category.CategoryUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindCategoryUseCase(useCase: CategoryUseCaseImpl): CategoryUseCase

}