package com.alexeymerov.radiostations.domain.usecase.settings.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteViewSettingsUseCase {

    fun getViewType(): Flow<ViewType>

    suspend fun setViewType(type: ViewType)

    enum class ViewType(val value: Int) {
        LIST(0), GRID_2_COLUMN(1), GRID_3_COLUMN(2)
    }

}

