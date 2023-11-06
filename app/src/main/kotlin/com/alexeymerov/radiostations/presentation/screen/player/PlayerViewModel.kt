package com.alexeymerov.radiostations.presentation.screen.player

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
) : BaseViewModel<PlayerViewModel.ViewState, PlayerViewModel.ViewAction, PlayerViewModel.ViewEffect>() {

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Playing)
    val playerState = _playerState.asStateFlow()

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.LoadAudio -> loadAudioLink(action.url)
            is ViewAction.PlayAudio -> _playerState.compareAndSet(PlayerState.Stopped, PlayerState.Playing)
            is ViewAction.StopAudio -> _playerState.compareAndSet(PlayerState.Playing, PlayerState.Stopped)
            is ViewAction.ToggleAudio -> _playerState.value =
                if (playerState.value == PlayerState.Playing) PlayerState.Stopped else PlayerState.Playing

            is ViewAction.ToggleFavorite -> toggleFavorite(action.id)
        }
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch(ioContext) {
            categoryUseCase.toggleFavorite(id)
        }
    }

    private fun loadAudioLink(url: String) {
        viewModelScope.launch(ioContext) {
            if (viewState.value == ViewState.Loading) {
                val audioDto = categoryUseCase.getAudioUrl(url)
                val newState = if (audioDto.isError) ViewState.Error else ViewState.ReadyToPlay(audioDto.url)
                setState(newState)
            }
        }
    }

    sealed class PlayerState(val lottieSpeed: Float) {
        data object Playing : PlayerState(-2f)
        data object Stopped : PlayerState(2f)
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object Error : ViewState
        class ReadyToPlay(val url: String) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        class LoadAudio(val url: String) : ViewAction

        data object ToggleAudio : ViewAction // it's simple at the moment, no need to separate it more
        data object PlayAudio : ViewAction // it's simple at the moment, no need to separate it more
        data object StopAudio : ViewAction

        data class ToggleFavorite(val id: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }

}

