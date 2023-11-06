package com.alexeymerov.radiostations.presentation

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeSettings: ThemeSettingsUseCase
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    override val viewState: StateFlow<ViewState>
        get() = themeSettings.getThemeState()
            .map { ViewState.Loaded(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ViewState.Loading
            )


    override fun createInitialState(): ViewState = ViewState.Loading

    override fun handleAction(action: ViewAction) {}

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data class Loaded(val themeState: ThemeState) : ViewState
    }

    sealed interface ViewAction : BaseViewAction

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}