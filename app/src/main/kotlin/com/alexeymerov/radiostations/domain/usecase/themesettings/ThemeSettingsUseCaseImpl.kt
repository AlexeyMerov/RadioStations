package com.alexeymerov.radiostations.domain.usecase.themesettings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.alexeymerov.radiostations.data.local.datastore.SettingsStore
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ColorState
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.DarkModeState
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ThemeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ThemeSettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore
) : ThemeSettingsUseCase {

    override fun getThemeState(): Flow<ThemeState> {
        return combine(
            flow = settingsStore.getIntPrefsFlow(DARK_THEME_KEY),
            flow2 = settingsStore.getBoolPrefsFlow(DYNAMIC_COLOR_KEY),
            flow3 = settingsStore.getIntPrefsFlow(COLOR_KEY)
        ) { darkThemeValue, dynamicColorValue, colorValue ->
            ThemeState(
                darkMode = DarkModeState.values()[darkThemeValue ?: 0],
                useDynamicColor = dynamicColorValue ?: true,
                colorState = ColorState.values()[colorValue ?: 0]
            )
        }
    }

    override suspend fun updateThemeState(state: ThemeState) {
        settingsStore.satPrefs(DARK_THEME_KEY, state.darkMode.ordinal)
        settingsStore.satPrefs(DYNAMIC_COLOR_KEY, state.useDynamicColor)
        settingsStore.satPrefs(COLOR_KEY, state.colorState.ordinal)
    }


    companion object {
        val DARK_THEME_KEY = intPreferencesKey("dark_theme")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        val COLOR_KEY = intPreferencesKey("color")
    }
}