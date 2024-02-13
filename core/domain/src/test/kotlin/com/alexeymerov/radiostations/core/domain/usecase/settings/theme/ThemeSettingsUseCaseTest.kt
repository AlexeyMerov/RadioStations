package com.alexeymerov.radiostations.core.domain.usecase.settings.theme

import com.alexeymerov.radiostations.core.datastore.FakeSettingsStore
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.*
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCaseImpl.Companion.COLOR_KEY
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCaseImpl.Companion.DARK_THEME_KEY
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCaseImpl.Companion.DYNAMIC_COLOR_KEY
import com.google.common.truth.Truth.*
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ThemeSettingsUseCaseTest {

    private lateinit var useCase: ThemeSettingsUseCase
    private lateinit var settingsStore: SettingsStore

    @Before
    fun setup() {
        settingsStore = spyk(FakeSettingsStore())
        useCase = ThemeSettingsUseCaseImpl(settingsStore)
    }

    private fun setCurrentColorTheme(type: ColorTheme) {
        every { settingsStore.getIntPrefsFlow(COLOR_KEY, any()) } returns flowOf(type.value)
    }

    private fun setCurrentDarkLightMode(type: DarkLightMode) {
        every { settingsStore.getIntPrefsFlow(DARK_THEME_KEY, any()) } returns flowOf(type.value)
    }

    private fun setCurrentDynamicColor(value: Boolean) {
        every { settingsStore.getBoolPrefsFlow(DYNAMIC_COLOR_KEY, any()) } returns flowOf(value)
    }

    @Test
    fun `get theme state returns valid data with default values`() = runTest {
        val themeState = useCase.getThemeState().first()

        assertThat(themeState.colorTheme).isEqualTo(ColorTheme.DEFAULT_BLUE)
        assertThat(themeState.darkLightMode).isEqualTo(DarkLightMode.SYSTEM)
        assertThat(themeState.useDynamicColor).isTrue()
    }

    @Test
    fun `get theme state if user changed prefs returns saved data`() = runTest {
        setCurrentColorTheme(ColorTheme.GREEN)
        setCurrentDarkLightMode(DarkLightMode.NIGHT)
        setCurrentDynamicColor(false)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.colorTheme).isEqualTo(ColorTheme.GREEN)
        assertThat(themeState.darkLightMode).isEqualTo(DarkLightMode.NIGHT)
        assertThat(themeState.useDynamicColor).isFalse()
    }

    @Test
    fun `get theme state if saved GREEN color returns GREEN value`() = runTest {
        setCurrentColorTheme(ColorTheme.GREEN)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.colorTheme).isEqualTo(ColorTheme.GREEN)
    }

    @Test
    fun `get theme state if saved ORANGE color returns ORANGE value`() = runTest {
        setCurrentColorTheme(ColorTheme.ORANGE)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.colorTheme).isEqualTo(ColorTheme.ORANGE)
    }

    @Test
    fun `get theme state if saved DEFBLUE color returns DEFBLUE value`() = runTest {
        setCurrentColorTheme(ColorTheme.DEFAULT_BLUE)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.colorTheme).isEqualTo(ColorTheme.DEFAULT_BLUE)
    }


    @Test
    fun `get theme state if saved TRUE returns TRUE`() = runTest {
        setCurrentDynamicColor(true)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.useDynamicColor).isTrue()
    }

    @Test
    fun `get theme state if saved FALSE returns FALSE`() = runTest {
        setCurrentDynamicColor(false)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.useDynamicColor).isFalse()
    }


    @Test
    fun `get theme state if saved SYSTEM returns SYSTEM value`() = runTest {
        setCurrentDarkLightMode(DarkLightMode.SYSTEM)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.darkLightMode).isEqualTo(DarkLightMode.SYSTEM)
    }

    @Test
    fun `get theme state if saved LIGHT returns LIGHT value`() = runTest {
        setCurrentDarkLightMode(DarkLightMode.LIGHT)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.darkLightMode).isEqualTo(DarkLightMode.LIGHT)
    }

    @Test
    fun `get theme state if saved DARK returns DARK value`() = runTest {
        setCurrentDarkLightMode(DarkLightMode.DARK)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.darkLightMode).isEqualTo(DarkLightMode.DARK)
    }

    @Test
    fun `get theme state if saved NIGHT returns NIGHT value`() = runTest {
        setCurrentDarkLightMode(DarkLightMode.NIGHT)

        val themeState = useCase.getThemeState().first()

        assertThat(themeState.darkLightMode).isEqualTo(DarkLightMode.NIGHT)
    }

    @Test
    fun `update theme state saves new data`() = runTest {
        val themeState = useCase.getThemeState().first()

        val newState = themeState.copy(
            darkLightMode = DarkLightMode.NIGHT,
            useDynamicColor = false,
            colorTheme = ColorTheme.ORANGE,
        )

        assertThat(themeState.colorTheme).isNotEqualTo(newState.colorTheme)
        assertThat(themeState.darkLightMode).isNotEqualTo(newState.darkLightMode)
        assertThat(themeState.useDynamicColor).isNotEqualTo(newState.useDynamicColor)

        useCase.updateThemeState(newState)

        val newThemeState = useCase.getThemeState().first()

        assertThat(newThemeState.colorTheme).isEqualTo(newState.colorTheme)
        assertThat(newThemeState.darkLightMode).isEqualTo(newState.darkLightMode)
        assertThat(newThemeState.useDynamicColor).isEqualTo(newState.useDynamicColor)
    }

    @Test
    fun `update theme with same data do nothing`() = runTest {
        val themeState = useCase.getThemeState().first()

        val colorTheme = themeState.colorTheme
        val darkLightMode = themeState.darkLightMode
        val useDynamicColor = themeState.useDynamicColor

        useCase.updateThemeState(themeState)

        val newThemeState = useCase.getThemeState().first()

        assertThat(newThemeState.colorTheme).isEqualTo(colorTheme)
        assertThat(newThemeState.darkLightMode).isEqualTo(darkLightMode)
        assertThat(newThemeState.useDynamicColor).isEqualTo(useDynamicColor)
    }
}