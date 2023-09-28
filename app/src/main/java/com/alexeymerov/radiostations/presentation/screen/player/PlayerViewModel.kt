package com.alexeymerov.radiostations.presentation.screen.player

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.R
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
class PlayerViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) :
    BaseViewModel<PlayerViewModel.ViewState, PlayerViewModel.ViewAction, PlayerViewModel.ViewEffect>() {

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Play)
    val playerState = _playerState.asStateFlow()

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.LoadAudio -> loadAudioLink(action.url)
            ViewAction.PlayAudio -> _playerState.compareAndSet(PlayerState.Stop, PlayerState.Play)
            ViewAction.StopAudio -> _playerState.compareAndSet(PlayerState.Play, PlayerState.Stop)
            ViewAction.ToggleAudio -> _playerState.value = if (playerState.value == PlayerState.Play) PlayerState.Stop else PlayerState.Play
        }
    }

    private fun loadAudioLink(url: String) {
        viewModelScope.launch {
            if (viewState.value == ViewState.Loading) {
                val audioDto = categoryUseCase.getAudioUrl(url)
                val newState = if (audioDto.isError) ViewState.Error else ViewState.ReadyToPlay(audioDto.url)
                setState(newState)
            }
        }
    }

    sealed class PlayerState(val icon: ImageVector, @StringRes val contentDescription: Int) {
        data object Play : PlayerState(Icons.Filled.Stop, R.string.stop)
        data object Stop : PlayerState(Icons.Filled.PlayArrow, R.string.play)
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
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

