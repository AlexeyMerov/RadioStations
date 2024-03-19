package com.alexeymerov.radiostations.feature.player.screen

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.di.Dispatcher
import com.alexeymerov.radiostations.core.common.di.RadioDispatchers
import com.alexeymerov.radiostations.core.common.toBase64
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.favorite.FavoriteUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoriteUseCase: FavoriteUseCase,
    private val playingUseCase: PlayingUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val audioUseCase: AudioUseCase,
    @Dispatcher(RadioDispatchers.IO) private val dispatcher: CoroutineDispatcher
) : BaseViewModel<PlayerViewModel.ViewState, PlayerViewModel.ViewAction, PlayerViewModel.ViewEffect>() {

    var isFavorite by mutableStateOf<Boolean?>(false)
    var title by mutableStateOf(String.EMPTY)
    var subTitle by mutableStateOf<String?>(null)

    private var itemId: String? = null

    private val currentPlayingAudioUrl: StateFlow<String?> = playingUseCase.getLastPlayingMediaItem()
        .map { it?.directUrl }
        .flowOn(dispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    init {
        val tuneId = checkNotNull(savedStateHandle.get<String>(Screens.Player.Const.ARG_TUNE_ID))

        viewModelScope.launch(dispatcher) {
            audioUseCase.getMediaItem(tuneId)?.let { currentItem ->
                Timber.d("loadAudioLink $currentItem")

                combine(
                    playingUseCase.getLastPlayingMediaItem(),
                    playingUseCase.getPlayerState()
                ) { item, state ->
                    val isSameItem = item?.tuneId == tuneId
                    isSameItem to state
                }.collectLatest { (isSameItem, systemPlayState) ->
                    val screenPlayState = when {
                        isSameItem && systemPlayState is PlayingUseCase.PlayerState.Playing -> ScreenPlayState.PLAYING
                        isSameItem && systemPlayState is PlayingUseCase.PlayerState.Loading -> ScreenPlayState.LOADING
                        else -> ScreenPlayState.STOPPED
                    }

                    setState(ViewState.ReadyToPlay(currentItem, screenPlayState))
                }

            } ?: setState(ViewState.Error)
        }

        viewModelScope.launch(dispatcher) {
            val itemDto = categoryUseCase.getByTuneId(tuneId)
            itemId = itemDto?.id
            withContext(Dispatchers.Main) {
                title = itemDto?.text ?: String.EMPTY
                subTitle = itemDto?.subTitle
                isFavorite = itemDto?.isFavorite
            }
        }
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("handleAction: ${action.javaClass.simpleName}")
        viewModelScope.launch(dispatcher) {
            when (action) {
                is ViewAction.ToggleFavorite -> toggleFavorite()
                is ViewAction.ChangeOrToggleAudio -> changeOrToggleAudio(action)
            }
        }
    }

    private suspend fun changeOrToggleAudio(action: ViewAction.ChangeOrToggleAudio) {
        if (action.mediaItem.directUrl == currentPlayingAudioUrl.value) {
            playingUseCase.togglePlayerPlayStop()
        } else {
            playingUseCase.setLastPlayingMedia(
                action.mediaItem.copy(
                    imageBase64 = action.bitmap?.toBase64()
                )
            )
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch(dispatcher) {
            isFavorite?.let { oldValue ->
                isFavorite = !oldValue
            }
            itemId?.let {
                favoriteUseCase.toggleFavorite(it)
            }
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object Error : ViewState
        data class ReadyToPlay(val item: AudioItemDto, val playState: ScreenPlayState) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data object ToggleFavorite : ViewAction
        data class ChangeOrToggleAudio(val mediaItem: AudioItemDto, val bitmap: Bitmap?) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

    enum class ScreenPlayState {
        STOPPED, LOADING, PLAYING
    }

}

