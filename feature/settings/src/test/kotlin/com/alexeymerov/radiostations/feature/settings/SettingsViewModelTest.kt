package com.alexeymerov.radiostations.feature.settings

import app.cash.turbine.test
import com.alexeymerov.radiostations.core.common.ColorTheme
import com.alexeymerov.radiostations.core.common.DarkLightMode
import com.alexeymerov.radiostations.core.common.UseDynamicColor
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.FakeConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.FakeThemeSettingsUseCase
import com.alexeymerov.radiostations.core.test.MainDispatcherRule
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewState
import com.google.common.truth.Truth.assertThat
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SettingsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var connectivitySettings: FakeConnectivitySettingsUseCase

    private lateinit var themeSettings: FakeThemeSettingsUseCase

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        connectivitySettings = FakeConnectivitySettingsUseCase()
        themeSettings = FakeThemeSettingsUseCase()

        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, dispatcherRule.testDispatcher)
    }

    @Test
    fun getState_whenConnectivityLoading_returnsLoadingState() = runTest {
        connectivitySettings.addDelayToFlow(100)
        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, dispatcherRule.testDispatcher)

        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(ViewState.Loading)
        }

    }

    @Test
    fun getState_whenThemeLoading_returnsLoadingState() = runTest {
        themeSettings.addDelayToFlow(100)
        viewModel = SettingsViewModel(themeSettings, connectivitySettings, firebaseAnalytics, dispatcherRule.testDispatcher)

        viewModel.viewState.test {
            assertThat(awaitItem()).isEqualTo(ViewState.Loading)
        }
    }

    @Test
    fun getState_whenDataLoaded_returnsLoadedState() = runTest {
        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.Loaded::class.java)
        }
    }

    @Test
    fun onAction_ChangeDarkMode_savesNewValue() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

            val loadedState = (state as ViewState.Loaded)
            val currentValue = loadedState.themeState.darkLightMode
            val newValue = DarkLightMode.NIGHT
            assertThat(currentValue).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.ChangeDarkMode(newValue))

            val newState = awaitItem()
            assertThat(newState).isInstanceOf(ViewState.Loaded::class.java)

            val updatedState = (newState as ViewState.Loaded)
            assertThat(updatedState.themeState.darkLightMode).isEqualTo(newValue)
        }
    }

    @Test
    fun onAction_ChangeDynamicColor_savesNewValue() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

            val loadedState = (state as ViewState.Loaded)
            val currentValue = loadedState.themeState.useDynamicColor
            val newValue = UseDynamicColor(false)
            assertThat(currentValue).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.ChangeDynamicColor(newValue))

            val newState = awaitItem()
            assertThat(newState).isInstanceOf(ViewState.Loaded::class.java)

            val updatedState = (newState as ViewState.Loaded)
            assertThat(updatedState.themeState.useDynamicColor.value).isEqualTo(newValue.value)
        }
    }

    @Test
    fun onAction_ChangeColorScheme_savesNewValue() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

            val loadedState = (state as ViewState.Loaded)
            val currentValue = loadedState.themeState.darkLightMode
            val newValue = ColorTheme.ORANGE
            assertThat(currentValue).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.ChangeColorScheme(newValue))

            val newState = awaitItem()
            assertThat(newState).isInstanceOf(ViewState.Loaded::class.java)

            val updatedState = (newState as ViewState.Loaded)
            assertThat(updatedState.themeState.colorTheme).isEqualTo(newValue)
        }
    }

    @Test
    fun onAction_ChangeConnection_savesNewValue() = runTest {
        viewModel.viewState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ViewState.Loaded::class.java)

            val loadedState = (state as ViewState.Loaded)
            val currentValue = loadedState.connectionStatus
            val newValue = ConnectionStatus.OFFLINE
            assertThat(currentValue).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.ChangeConnection(newValue))

            val newState = awaitItem()
            assertThat(newState).isInstanceOf(ViewState.Loaded::class.java)

            val updatedState = (newState as ViewState.Loaded)
            assertThat(updatedState.connectionStatus).isEqualTo(newValue)
        }
    }

}