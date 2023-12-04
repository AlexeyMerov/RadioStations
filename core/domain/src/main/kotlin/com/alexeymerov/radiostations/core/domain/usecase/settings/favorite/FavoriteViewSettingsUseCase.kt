package com.alexeymerov.radiostations.core.domain.usecase.settings.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteViewSettingsUseCase {

    fun getViewType(): Flow<ViewType>

    suspend fun setViewType(type: ViewType)

    enum class ViewType(val columnCount: Int) {
        LIST(1), GRID_2_COLUMN(2), GRID_3_COLUMN(3)
    }

}

