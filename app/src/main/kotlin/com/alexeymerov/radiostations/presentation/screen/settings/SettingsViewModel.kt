package com.alexeymerov.radiostations.presentation.screen.settings

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ColorTheme
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.DarkLightMode
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeSettings: ThemeSettingsUseCase,
    private val connectivitySettings: ConnectivitySettingsUseCase
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    private lateinit var currentThemeState: ThemeState

    override val viewState: StateFlow<ViewState>
        get() = combine(
            flow = themeSettings.getThemeState(),
            flow2 = connectivitySettings.getConnectionStatusFlow()
        ) { themeState, connectionStatus ->
            currentThemeState = themeState
            return@combine ViewState.Loaded(themeState = themeState, connectionStatus = connectionStatus)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ViewState.Loading
            )

    override fun createInitialState(): ViewState = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        viewModelScope.launch(ioContext) {
            when (action) {
                is ViewAction.ChangeDarkMode -> changeChangeDarkMode(action.value)
                is ViewAction.ChangeDynamicColor -> changeChangeDynamicColor(action.useDynamic)
                is ViewAction.ChangeColorScheme -> changeChangeColorScheme(action.value)
                is ViewAction.ChangeConnection -> connectivitySettings.setConnectionStatus(action.status)
            }
        }
    }

    private suspend fun changeChangeDarkMode(value: DarkLightMode) {
        themeSettings.updateThemeState(
            currentThemeState.copy(darkLightMode = value)
        )
    }

    private suspend fun changeChangeDynamicColor(useDynamic: Boolean) {
        themeSettings.updateThemeState(
            currentThemeState.copy(useDynamicColor = useDynamic)
        )
    }

    private suspend fun changeChangeColorScheme(value: ColorTheme) {
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
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}