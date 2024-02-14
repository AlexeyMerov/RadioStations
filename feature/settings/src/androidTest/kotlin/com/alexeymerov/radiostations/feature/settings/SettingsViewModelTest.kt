package com.alexeymerov.radiostations.feature.settings

import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.FakeConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.FakeThemeSettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewState
import com.google.common.truth.Truth.assertThat
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var connectivitySettings: FakeConnectivitySettingsUseCase

    private lateinit var themeSettings: FakeThemeSettingsUseCase

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        connectivitySettings = FakeConnectivitySettingsUseCase()
        themeSettings = FakeThemeSettingsUseCase()

        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, UnconfinedTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getState_whenConnectivityLoading_returnsLoadingState() = runTest {
        connectivitySettings.addDelayToFlow(100)
        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, UnconfinedTestDispatcher())

        assertThat(viewModel.viewState.value).isEqualTo(ViewState.Loading)
    }

    @Test
    fun getState_whenThemeLoading_returnsLoadingState() = runTest {
        themeSettings.addDelayToFlow(100)
        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, UnconfinedTestDispatcher())

        assertThat(viewModel.viewState.value).isEqualTo(ViewState.Loading)
    }

    @Test
    fun getState_whenDataLoaded_returnsLoadedState() = runTest {
        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, UnconfinedTestDispatcher())

        advanceUntilIdle()

        assertThat(viewModel.viewState.value).isInstanceOf(ViewState.Loaded::class.java)
    }

    @Test
    fun onAction_ChangeDarkMode_savesNewValue() = runTest {
        val state = viewModel.viewState.value
        assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

        val loadedState = (state as ViewState.Loaded)
        val currentValue = loadedState.themeState.darkLightMode
        val newValue = ThemeSettingsUseCase.DarkLightMode.NIGHT
        assertThat(currentValue).isNotEqualTo(newValue)

        viewModel.setAction(ViewAction.ChangeDarkMode(newValue))

        advanceUntilIdle()

        val updatedState = (viewModel.viewState.first() as ViewState.Loaded)
        assertThat(updatedState.themeState.darkLightMode).isEqualTo(newValue)
    }

    @Test
    fun onAction_ChangeDynamicColor_savesNewValue() = runTest {
        val state = viewModel.viewState.value
        assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

        val loadedState = (state as ViewState.Loaded)
        val currentValue = loadedState.themeState.useDynamicColor
        val newValue = false
        assertThat(currentValue).isNotEqualTo(newValue)

        viewModel.setAction(ViewAction.ChangeDynamicColor(newValue))

        advanceUntilIdle()

        val updatedState = (viewModel.viewState.first() as ViewState.Loaded)
        assertThat(updatedState.themeState.useDynamicColor).isEqualTo(newValue)
    }

    @Test
    fun onAction_ChangeColorScheme_savesNewValue() = runTest {
        val state = viewModel.viewState.value
        assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

        val loadedState = (state as ViewState.Loaded)
        val currentValue = loadedState.themeState.darkLightMode
        val newValue = ThemeSettingsUseCase.ColorTheme.ORANGE
        assertThat(currentValue).isNotEqualTo(newValue)

        viewModel.setAction(ViewAction.ChangeColorScheme(newValue))

        advanceUntilIdle()

        val updatedState = (viewModel.viewState.first() as ViewState.Loaded)
        assertThat(updatedState.themeState.colorTheme).isEqualTo(newValue)
    }

    @Test
    fun onAction_ChangeConnection_savesNewValue() = runTest {
        val state = viewModel.viewState.value
        assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

        val loadedState = (state as ViewState.Loaded)
        val currentValue = loadedState.connectionStatus
        val newValue = ConnectionStatus.OFFLINE
        assertThat(currentValue).isNotEqualTo(newValue)

        viewModel.setAction(ViewAction.ChangeConnection(newValue))

        advanceUntilIdle()

        val updatedState = (viewModel.viewState.first() as ViewState.Loaded)
        assertThat(updatedState.connectionStatus).isEqualTo(newValue)
    }

}