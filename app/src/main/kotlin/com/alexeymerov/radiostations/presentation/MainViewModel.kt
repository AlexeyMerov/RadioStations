package com.alexeymerov.radiostations.presentation

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.connectivity.ConnectionMonitor
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    themeSettings: ThemeSettingsUseCase,
    private val audioUseCase: AudioUseCase,
    connectionMonitor: ConnectionMonitor
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    val isNetworkAvailable: StateFlow<Boolean> = connectionMonitor.connectionStatusFlow

    override val viewState: StateFlow<ViewState> = themeSettings.getThemeState()
        .map { ViewState.Loaded(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ViewState.Loading
        )

    val playerState: StateFlow<PlayerState> = audioUseCase.getPlayerState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlayerState.EMPTY
        )

    val currentAudioItem: StateFlow<AudioItemDto?> = audioUseCase.getLastPlayingMediaItem()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun createInitialState(): ViewState = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${action.javaClass.simpleName}")

        viewModelScope.launch(ioContext) {
            when (action) {
                is ViewAction.PlayAudio -> audioUseCase.updatePlayerState(PlayerState.PLAYING)
                is ViewAction.StopAudio -> audioUseCase.updatePlayerState(PlayerState.STOPPED)
                is ViewAction.ToggleAudio -> audioUseCase.togglePlayerPlayStop()
                is ViewAction.ChangeAudio -> audioUseCase.setLastPlayingMedia(action.mediaItem)
                is ViewAction.NukePlayer -> audioUseCase.updatePlayerState(PlayerState.EMPTY)
            }
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data class Loaded(val themeState: ThemeState) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class ChangeAudio(val mediaItem: AudioItemDto) : ViewAction
        data object ToggleAudio : ViewAction
        data object PlayAudio : ViewAction
        data object StopAudio : ViewAction
        data object NukePlayer : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

}