package com.alexeymerov.radiostations.core.domain.usecase.settings.theme

import com.alexeymerov.radiostations.core.common.ColorTheme
import com.alexeymerov.radiostations.core.common.DarkLightMode
import com.alexeymerov.radiostations.core.common.ThemeState
import com.alexeymerov.radiostations.core.common.UseDynamicColor
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ThemeSettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore
) : ThemeSettingsUseCase {

    override fun getThemeState(): Flow<ThemeState> {
        return combine(
            flow = settingsStore.getIntPrefsFlow(DARK_THEME_KEY, defValue = DarkLightMode.SYSTEM.value),
            flow2 = settingsStore.getBoolPrefsFlow(DYNAMIC_COLOR_KEY, defValue = true),
            flow3 = settingsStore.getIntPrefsFlow(COLOR_KEY, defValue = 0)
        ) { darkLightId, dynamicColorValue, colorThemeId ->
            val darkLightMode = DarkLightMode.entries.first { it.value == darkLightId }
            val colorTheme = ColorTheme.entries.first { it.value == colorThemeId }
            ThemeState(
                darkLightMode = darkLightMode,
                useDynamicColor = UseDynamicColor(dynamicColorValue),
                colorTheme = colorTheme
            )
        }
    }

    override suspend fun updateThemeState(state: ThemeState) {
        settingsStore.setIntPrefs(DARK_THEME_KEY, state.darkLightMode.value)
        settingsStore.setBoolPrefs(DYNAMIC_COLOR_KEY, state.useDynamicColor.value)
        settingsStore.setIntPrefs(COLOR_KEY, state.colorTheme.value)
    }


    internal companion object {
        const val DARK_THEME_KEY = "dark_theme"
        const val DYNAMIC_COLOR_KEY = "dynamic_color"
        const val COLOR_KEY = "color"
    }
}