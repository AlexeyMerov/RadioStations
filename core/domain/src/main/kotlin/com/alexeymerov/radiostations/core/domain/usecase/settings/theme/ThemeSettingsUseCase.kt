package com.alexeymerov.radiostations.core.domain.usecase.settings.theme

import kotlinx.coroutines.flow.Flow

interface ThemeSettingsUseCase {

    fun getThemeState(): Flow<ThemeState>

    suspend fun updateThemeState(state: ThemeState)

    data class ThemeState(
        val darkLightMode: DarkLightMode = DarkLightMode.SYSTEM,
        val useDynamicColor: Boolean = true,
        val colorTheme: ColorTheme = ColorTheme.DEFAULT_BLUE
    )

    enum class DarkLightMode(val value: Int) {
        SYSTEM(0), LIGHT(1), DARK(2), NIGHT(3)
    }

    enum class ColorTheme(val value: Int) {
        DEFAULT_BLUE(0), GREEN(1), ORANGE(2)
    }
}