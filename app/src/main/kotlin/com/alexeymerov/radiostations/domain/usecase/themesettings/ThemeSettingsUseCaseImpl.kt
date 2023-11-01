package com.alexeymerov.radiostations.domain.usecase.themesettings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.alexeymerov.radiostations.data.local.datastore.SettingsStore
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ColorTheme
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.DarkLightMode
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ThemeState
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
        settingsStore.satPrefs(DARK_THEME_KEY, state.darkLightMode.id)
        settingsStore.satPrefs(DYNAMIC_COLOR_KEY, state.useDynamicColor)
        settingsStore.satPrefs(COLOR_KEY, state.colorTheme.id)
    }


    companion object {
        val DARK_THEME_KEY = intPreferencesKey("dark_theme")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        val COLOR_KEY = intPreferencesKey("color")
    }
}