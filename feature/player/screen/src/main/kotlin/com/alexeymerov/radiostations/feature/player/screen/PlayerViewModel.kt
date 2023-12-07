package com.alexeymerov.radiostations.feature.player.screen

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val audioUseCase: AudioUseCase
) : BaseViewModel<PlayerViewModel.ViewState, PlayerViewModel.ViewAction, PlayerViewModel.ViewEffect>() {

    val currentAudioItem: StateFlow<AudioItemDto?> = audioUseCase.getLastPlayingMediaItem()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${action.javaClass.simpleName}")
        viewModelScope.launch(ioContext) {
            when (action) {
                is ViewAction.ToggleFavorite -> toggleFavorite(action.id)
                is ViewAction.LoadAudio -> loadAudioLink(action.url)
                is ViewAction.ToggleAudio -> audioUseCase.togglePlayerPlayStop()
                is ViewAction.ChangeAudio -> audioUseCase.setLastPlayingMedia(action.mediaItem)
            }
        }
    }

    private fun loadAudioLink(originalUrl: String) {
        viewModelScope.launch(ioContext) {
            if (viewState.value == ViewState.Loading) {
                audioUseCase.getMediaItem(originalUrl)?.let { currentItem ->
                    Timber.d("loadAudioLink $currentItem")

                    combine(
                        flow = audioUseCase.getLastPlayingMediaItem(),
                        flow2 = audioUseCase.getPlayerState()
                    ) { item, state ->
                        val isSameItem = item?.parentUrl == originalUrl
                        val isPlaying = isSameItem && state == AudioUseCase.PlayerState.PLAYING
                        val isLoading = isSameItem && state == AudioUseCase.PlayerState.LOADING
                        isPlaying to isLoading
                    }.collectLatest { pair ->
                        setState(ViewState.ReadyToPlay(currentItem, pair.first, pair.second))
                    }

                } ?: setState(ViewState.Error)
            }
        }
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch(ioContext) {
            audioUseCase.toggleFavorite(id)
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object Error : ViewState
        data class ReadyToPlay(val item: AudioItemDto, val isPlaying: Boolean, val isLoading: Boolean) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class ToggleFavorite(val id: String) : ViewAction
        data class LoadAudio(val url: String) : ViewAction

        data class ChangeAudio(val mediaItem: AudioItemDto) : ViewAction
        data object ToggleAudio : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

}
