package com.alexeymerov.radiostations.core.domain.usecase.settings.favorite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavoriteViewSettingsUseCase : FavoriteViewSettingsUseCase {

    private val currentViewType = MutableStateFlow(FavoriteViewSettingsUseCase.ViewType.LIST)

    override fun getViewType(): Flow<FavoriteViewSettingsUseCase.ViewType> {
        return currentViewType
    }

    override suspend fun setViewType(type: FavoriteViewSettingsUseCase.ViewType) {
        currentViewType.value = type
    }
}