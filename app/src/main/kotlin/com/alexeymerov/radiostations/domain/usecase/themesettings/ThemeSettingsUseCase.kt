package com.alexeymerov.radiostations.domain.usecase.themesettings

import kotlinx.coroutines.flow.Flow

interface ThemeSettingsUseCase {

    fun getThemeState(): Flow<ThemeState>

    suspend fun updateThemeState(state: ThemeState)

    data class ThemeState(
        val darkMode: DarkModeState = DarkModeState.System,
        val useDynamicColor: Boolean = true,
        val colorState: ColorState = ColorState.DefaultBlue
    )

    enum class DarkModeState {
        System, Dark, Light
    }

    enum class ColorState {
        DefaultBlue, Green, Orange
    }
}