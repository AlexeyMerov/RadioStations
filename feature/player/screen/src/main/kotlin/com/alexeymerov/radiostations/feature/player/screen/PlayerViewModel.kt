package com.alexeymerov.radiostations.feature.player.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.core.ui.navigation.Screens
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
    savedStateHandle: SavedStateHandle,
    private val audioUseCase: AudioUseCase
) : BaseViewModel<PlayerViewModel.ViewState, PlayerViewModel.ViewAction, PlayerViewModel.ViewEffect>() {

    var isFavorite by mutableStateOf(savedStateHandle.get<Boolean>(Screens.Player.Const.ARG_IS_FAV) ?: false)

    val currentAudioItem: StateFlow<AudioItemDto?> = audioUseCase.getLastPlayingMediaItem()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ PlayerViewModel ] handleAction: ${action.javaClass.simpleName}")
        viewModelScope.launch(ioContext) {
            when (action) {
                is ViewAction.ToggleFavorite -> toggleFavorite(action.id)
                is ViewAction.LoadAudio -> loadAudioLink(action.url)
                is ViewAction.ToggleAudio -> audioUseCase.togglePlayerPlayStop()
                is ViewAction.ChangeAudio -> audioUseCase.setLastPlayingMedia(action.mediaItem)
                is ViewAction.LoadStationInfo -> loadStationInfo(action)
            }
        }
    }

    private suspend fun loadStationInfo(action: ViewAction.LoadStationInfo) {
        val station = audioUseCase.getByUrl(action.parentUrl)
        if (station == null) {
            Timber.e("loadStationInfo cant find an item")
            setState(ViewState.Error)
            return
        }
        isFavorite = station.isFavorite
        setState(ViewState.Loaded(station))
    }

    private fun loadAudioLink(originalUrl: String) {
        setState(ViewState.Loading)

        viewModelScope.launch(ioContext) {
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
                }.collectLatest { (isPlaying, isLoading) ->
                    setState(ViewState.ReadyToPlay(currentItem, isPlaying, isLoading))
                }

            } ?: setState(ViewState.Error)
        }
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch(ioContext) {
            isFavorite = !isFavorite
            audioUseCase.toggleFavorite(id)
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object Error : ViewState
        data class ReadyToPlay(val item: AudioItemDto, val isPlaying: Boolean, val isLoading: Boolean) : ViewState
        data class Loaded(val item: CategoryItemDto) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class ToggleFavorite(val id: String) : ViewAction
        data class LoadAudio(val url: String) : ViewAction
        data class LoadStationInfo(val parentUrl: String) : ViewAction

        data class ChangeAudio(val mediaItem: AudioItemDto) : ViewAction
        data object ToggleAudio : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

}

