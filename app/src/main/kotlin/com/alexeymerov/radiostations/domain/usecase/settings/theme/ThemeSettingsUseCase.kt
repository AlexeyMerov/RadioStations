package com.alexeymerov.radiostations.domain.usecase.settings.theme

import kotlinx.coroutines.flow.Flow

interface ThemeSettingsUseCase {

    fun getThemeState(): Flow<ThemeState>

    suspend fun updateThemeState(state: ThemeState)

    data class ThemeState(
        val darkLightMode: DarkLightMode = DarkLightMode.System,
        val useDynamicColor: Boolean = true,
        val colorTheme: ColorTheme = ColorTheme.DefaultBlue
    )

    enum class DarkLightMode(val id: Int) {
        System(0), Light(1), Dark(2), Night(3)
    }

    enum class ColorTheme(val id: Int) {
        DefaultBlue(0), Green(1), Orange(2)
    }
}