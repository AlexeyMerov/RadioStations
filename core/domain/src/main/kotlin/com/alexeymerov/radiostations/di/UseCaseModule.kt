package com.alexeymerov.radiostations.di


import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapper
import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapperImpl
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.profile.ProfileUsaCase
import com.alexeymerov.radiostations.domain.usecase.profile.ProfileUsaCaseImpl
import com.alexeymerov.radiostations.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.connectivity.ConnectivitySettingsUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.favorite.FavoriteViewSettingsUseCaseImpl
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [UseCaseModule.Mapper::class])
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindCategoryUseCase(useCase: CategoryUseCaseImpl): CategoryUseCase

    @Binds
    @Singleton
    abstract fun bindFavoriteViewSettingsUseCase(useCase: FavoriteViewSettingsUseCaseImpl): FavoriteViewSettingsUseCase

    @Binds
    @Singleton
    abstract fun bindThemeSettingsUseCase(useCase: ThemeSettingsUseCaseImpl): ThemeSettingsUseCase

    @Binds
    @Singleton
    abstract fun bindConnectivitySettingsUseCase(useCase: ConnectivitySettingsUseCaseImpl): ConnectivitySettingsUseCase

    @Binds
    @Singleton
    abstract fun bindMediaUseCase(useCase: AudioUseCaseImpl): AudioUseCase

    @Binds
    @Singleton
    abstract fun bindProfileUsaCase(useCase: ProfileUsaCaseImpl): ProfileUsaCase

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Mapper {

        @Binds
        @Singleton
        abstract fun bindDtoCategoriesMapper(mapper: DtoCategoriesMapperImpl): DtoCategoriesMapper

    }

}