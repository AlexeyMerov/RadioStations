package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapper
import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapperImpl
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCaseImpl
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
    abstract fun bindFavoriteViewSettingsUseCase(useCase: FavoriteViewSettingsUseCaseImpl): FavoriteViewSettingsUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindThemeSettingsUseCase(useCase: ThemeSettingsUseCaseImpl): ThemeSettingsUseCase

    @Module
    @InstallIn(ViewModelComponent::class)
    abstract class Mapper {

        @Binds
        @ViewModelScoped
        abstract fun bindDtoCategoriesMapper(mapper: DtoCategoriesMapperImpl): DtoCategoriesMapper

    }

}