package com.alexeymerov.radiostations.presentation.screen.player

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val audioUseCase: AudioUseCase
) : BaseViewModel<PlayerViewModel.ViewState, PlayerViewModel.ViewAction, PlayerViewModel.ViewEffect>() {

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.ToggleFavorite -> toggleFavorite(action.id)
            is ViewAction.LoadAudio -> loadAudioLink(action.url)
        }
    }

    private fun loadAudioLink(originalUrl: String) {
        viewModelScope.launch(ioContext) {
            if (viewState.value == ViewState.Loading) {
                audioUseCase.getMediaItem(originalUrl)?.let { currentItem ->
                    Timber.d("loadAudioLink $currentItem")

                    audioUseCase.getLastPlayingMediaItem().collectLatest { lastPlayingMediaItem ->
                        val isPlaying = lastPlayingMediaItem?.parentUrl == originalUrl
                        setState(ViewState.ReadyToPlay(currentItem, isPlaying))
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
        data class ReadyToPlay(val item: AudioItemDto, val isPlaying: Boolean) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class ToggleFavorite(val id: String) : ViewAction
        data class LoadAudio(val url: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

}

