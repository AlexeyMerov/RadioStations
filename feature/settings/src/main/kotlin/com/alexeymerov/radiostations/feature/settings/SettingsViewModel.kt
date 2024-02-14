package com.alexeymerov.radiostations.feature.settings

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.analytics.AnalyticsEvents
import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ColorTheme
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.DarkLightMode
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewEffect
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeSettings: ThemeSettingsUseCase,
    private val connectivitySettings: ConnectivitySettingsUseCase,
    private val analytics: FirebaseAnalytics,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    private lateinit var currentThemeState: ThemeState

    init {
        viewModelScope.launch {
            combine(
                themeSettings.getThemeState(),
                connectivitySettings.getConnectionStatusFlow(),
                ViewState::Loaded
            )
                .collectLatest {
                    currentThemeState = it.themeState
                    setState(it)
                }
        }
    }

    override fun createInitialState(): ViewState = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("SettingsViewModel handleAction ${action.javaClass.simpleName}")
        viewModelScope.launch(dispatcher) {
            when (action) {
                is ViewAction.ChangeDarkMode -> changeChangeDarkMode(action.value)
                is ViewAction.ChangeDynamicColor -> changeChangeDynamicColor(action.useDynamic)
                is ViewAction.ChangeColorScheme -> changeChangeColorScheme(action.value)
                is ViewAction.ChangeConnection -> changeConnectionStatus(action.status)
            }
        }
    }

    private suspend fun changeConnectionStatus(status: ConnectionStatus) {
        connectivitySettings.forceConnectionStatus(status)
    }

    private suspend fun changeChangeDarkMode(value: DarkLightMode) {
        analytics.logEvent(AnalyticsEvents.THEME_SETTINGS) {
            param(AnalyticsParams.DARK_MODE, value.name.lowercase())
        }
        themeSettings.updateThemeState(
            currentThemeState.copy(darkLightMode = value)
        )
    }

    private suspend fun changeChangeDynamicColor(useDynamic: Boolean) {
        analytics.logEvent(AnalyticsEvents.THEME_SETTINGS) {
            param(AnalyticsParams.DYNAMIC_COLOR, useDynamic.toString())
        }
        themeSettings.updateThemeState(
            currentThemeState.copy(useDynamicColor = useDynamic)
        )
    }

    private suspend fun changeChangeColorScheme(value: ColorTheme) {
        analytics.logEvent(AnalyticsEvents.THEME_SETTINGS) {
            param(AnalyticsParams.COLOR_SCHEME, value.name.lowercase())
        }
        themeSettings.updateThemeState(
            currentThemeState.copy(colorTheme = value)
        )
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data class Loaded(val themeState: ThemeState, val connectionStatus: ConnectionStatus) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class ChangeDarkMode(val value: DarkLightMode) : ViewAction
        data class ChangeDynamicColor(val useDynamic: Boolean) : ViewAction
        data class ChangeColorScheme(val value: ColorTheme) : ViewAction

        data class ChangeConnection(val status: ConnectionStatus) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

}