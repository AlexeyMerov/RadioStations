package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapper
import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapperImpl
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.usersettings.UserSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.usersettings.UserSettingsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module(includes = [UseCaseModule.Mapper::class])
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun bindCategoryUseCase(useCase: CategoryUseCaseImpl): CategoryUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindSettingsUseCase(useCase: UserSettingsUseCaseImpl): UserSettingsUseCase

    @Module
    @InstallIn(ViewModelComponent::class)
    abstract class Mapper {

        @Binds
        @ViewModelScoped
        abstract fun bindDtoCategoriesMapper(mapper: DtoCategoriesMapperImpl): DtoCategoriesMapper

    }

}