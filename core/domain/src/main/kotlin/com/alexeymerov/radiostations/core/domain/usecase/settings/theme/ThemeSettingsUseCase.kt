package com.alexeymerov.radiostations.core.domain.usecase.settings.theme

import com.alexeymerov.radiostations.core.common.ThemeState
import kotlinx.coroutines.flow.Flow

interface ThemeSettingsUseCase {

    fun getThemeState(): Flow<ThemeState>

    suspend fun updateThemeState(state: ThemeState)

}