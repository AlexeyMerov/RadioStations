package com.alexeymerov.radiostations.presentation

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

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
                is ViewAction.ChangeAudio -> handleAudioChanged(action.mediaItem)

                ViewAction.NukePlayer -> audioUseCase.updatePlayerState(PlayerState.EMPTY)
            }
        }
    }

    private suspend fun handleAudioChanged(newMediaUrl: AudioItemDto) {
        Timber.d("new url $newMediaUrl")
        audioUseCase.setLastPlayingMediaItem(newMediaUrl)

        // temp workaround since we handling isPlaying in service and it works slower then the bottom line.
        // Should be fixed with Buffering state implementation
        delay(500)
        audioUseCase.updatePlayerState(PlayerState.PLAYING)
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
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}