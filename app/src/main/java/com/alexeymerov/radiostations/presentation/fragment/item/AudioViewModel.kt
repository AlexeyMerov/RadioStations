package com.alexeymerov.radiostations.presentation.fragment.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun loadAudioLink(url: String) { // todo make actions and handle player
        viewModelScope.launch {
            val audioDto = categoryUseCase.getAudioUrl(url)
            val newState = if (audioDto.isError) ViewState.Error else ViewState.ReadyToPlay(audioDto.url)
            setNewState(newState)
        }
    }

    private fun setNewState(state: ViewState) {
        _viewState.value = state
    }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    sealed interface ViewState {
        object Loading : ViewState
        object Error : ViewState
        class ReadyToPlay(val url: String) : ViewState // todo handle player states
    }

}

