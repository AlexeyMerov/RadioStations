package com.alexeymerov.radiostations.domain.usecase.usersettings

import kotlinx.coroutines.flow.Flow

interface UserSettingsUseCase {

    fun getViewType(): Flow<ViewType>

    suspend fun setNextViewType(currentValue: ViewType)

    enum class ViewType(val value: Int) {
        LIST(1), GRID_2_COLUMN(2), GRID_3_COLUMN(3)
    }

}

