package com.alexeymerov.radiostations.domain.usecase.settings.theme

import com.alexeymerov.radiostations.data.local.datastore.SettingsStore
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ColorTheme
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.DarkLightMode
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ThemeSettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore
) : ThemeSettingsUseCase {

    override fun getThemeState(): Flow<ThemeState> {
        return combine(
            flow = settingsStore.getIntPrefsFlow(DARK_THEME_KEY, defValue = 0),
            flow2 = settingsStore.getBoolPrefsFlow(DYNAMIC_COLOR_KEY, defValue = true),
            flow3 = settingsStore.getIntPrefsFlow(COLOR_KEY, defValue = 0)
        ) { darkLightId, dynamicColorValue, colorThemeId ->
            val darkLightMode = DarkLightMode.values().first { it.id == darkLightId }
            val colorTheme = ColorTheme.values().first { it.id == colorThemeId }
            ThemeState(
                darkLightMode = darkLightMode,
                useDynamicColor = dynamicColorValue,
                colorTheme = colorTheme
            )
        }
    }

    override suspend fun updateThemeState(state: ThemeState) {
        settingsStore.setIntPrefs(DARK_THEME_KEY, state.darkLightMode.id)
        settingsStore.setBoolPrefs(DYNAMIC_COLOR_KEY, state.useDynamicColor)
        settingsStore.setIntPrefs(COLOR_KEY, state.colorTheme.id)
    }


    companion object {
        const val DARK_THEME_KEY = "dark_theme"
        const val DYNAMIC_COLOR_KEY = "dynamic_color"
        const val COLOR_KEY = "color"
    }
}