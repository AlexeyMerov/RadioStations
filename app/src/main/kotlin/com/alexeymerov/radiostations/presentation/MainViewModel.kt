package com.alexeymerov.radiostations.presentation

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Empty)
    val playerState = _playerState.asStateFlow()

    // in case service is playing on clean start
    fun getPlayerState(isPlaying: Boolean = false): StateFlow<PlayerState> {
        Timber.d("getPlayerState ${_playerState.value}")
        if (isPlaying && _playerState.value !is PlayerState.Playing) {
            _playerState.value = PlayerState.Playing
        }
        return _playerState.asStateFlow()
    }

    val currentAudioItem: StateFlow<AudioItemDto?> = audioUseCase.getLastPlayingMediaItem()
        .onEach {
            Timber.d("currentMediaItem $it")
            if (it == null) {
                _playerState.value = PlayerState.Empty
            } else if (_playerState.value is PlayerState.Empty) {
                _playerState.value = PlayerState.Stopped
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    override fun createInitialState(): ViewState = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${action.javaClass.simpleName}")

        viewModelScope.launch(ioContext) {
            _playerState.value = when (action) {
                is ViewAction.PlayAudio -> PlayerState.Playing
                is ViewAction.StopAudio -> PlayerState.Stopped
                is ViewAction.ToggleAudio -> {
                    when (_playerState.value) {
                        is PlayerState.Stopped -> PlayerState.Playing
                        else -> PlayerState.Stopped
                    }
                }

                is ViewAction.ChangeAudio -> {
                    val newMediaUrl = action.mediaItem
                    Timber.d("new url $newMediaUrl")

                    if (currentAudioItem.value != newMediaUrl) {
                        audioUseCase.setLastPlayingMediaItem(newMediaUrl)
                    }

                    when (newMediaUrl) {
                        null -> PlayerState.Empty
                        else -> PlayerState.Playing
                    }
                }
            }
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data class Loaded(val themeState: ThemeState) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class ChangeAudio(val mediaItem: AudioItemDto?) : ViewAction
        data object ToggleAudio : ViewAction
        data object PlayAudio : ViewAction
        data object StopAudio : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

    sealed interface PlayerState {
        data object Empty : PlayerState
        data object Playing : PlayerState
        data object Stopped : PlayerState
    }

}