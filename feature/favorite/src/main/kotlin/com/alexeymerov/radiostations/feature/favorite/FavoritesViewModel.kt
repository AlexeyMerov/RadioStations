package com.alexeymerov.radiostations.feature.favorite

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.ViewType
import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.ViewAction
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.ViewEffect
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val audioUseCase: AudioUseCase,
    private val settingsUseCase: FavoriteViewSettingsUseCase,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    private val recentlyUnfavorited = mutableSetOf<CategoryItemDto>()
    private val selectedItems = mutableSetOf<CategoryItemDto>()
    var selectedItemsCount = mutableIntStateOf(0)

    private var undoJobTimeout: Job? = null

    init {
        viewModelScope.launch(dispatcher) {
            settingsUseCase.getViewType()
                .catch { handleError(it) }
                .combine(audioUseCase.getFavorites()) { viewType, dto ->
                    validateNewData(viewType, dto)
                }
                .collectLatest {
                    setState(it)
                }
        }
    }

    override fun setAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new action: ${action.javaClass.simpleName}")
        super.setAction(action)
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.name} ] handleAction: ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.SetViewType -> setViewType(action.type)
            is ViewAction.SelectItem -> selectItem(action.item)
            is ViewAction.Unfavorite -> unfavorite(action.item)
            is ViewAction.UnfavoriteSelected -> unfavoriteSelected()
            is ViewAction.UndoRecentUnfavorite -> undoRecentUnfavorite()
        }
    }

    private fun undoRecentUnfavorite() {
        viewModelScope.launch(dispatcher) {
            recentlyUnfavorited.forEach {
                audioUseCase.setFavorite(it)
            }
            recentlyUnfavorited.clear()
        }
    }

    private fun selectItem(item: CategoryItemDto) {
        viewModelScope.launch(dispatcher) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                selectedItemsCount.intValue--
            } else {
                selectedItems.add(item)
                selectedItemsCount.intValue++
            }
        }
    }

    private fun setViewType(type: ViewType) {
        viewModelScope.launch(dispatcher) {
            settingsUseCase.setViewType(type)
        }
    }

    private fun unfavorite(item: CategoryItemDto) {
        viewModelScope.launch(dispatcher) {
            audioUseCase.unfavorite(item)
            recentlyUnfavorited.clear()
            recentlyUnfavorited.add(item)
            showUndoSnackbar()
        }
    }

    private fun unfavoriteSelected() {
        viewModelScope.launch(dispatcher) {
            recentlyUnfavorited.clear()
            selectedItems.forEach {
                audioUseCase.unfavorite(it)
                recentlyUnfavorited.add(it)
            }
            selectedItemsCount.intValue = 0
            selectedItems.clear()
            showUndoSnackbar()
        }
    }

    private suspend fun showUndoSnackbar() {
        Timber.d("[ ${object {}.javaClass.name} ] showUndoSnackbar size: ${recentlyUnfavorited.size}")
        setEffect(ViewEffect.ShowUnfavoriteToast(recentlyUnfavorited.size))

        undoJobTimeout?.cancel()
        undoJobTimeout = viewModelScope.launch {
            delay(5000)
            recentlyUnfavorited.clear()
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "[ ${object {}.javaClass.enclosingMethod?.name} ] handleError")
        if (viewState.value == ViewState.Loading) {
            setState(ViewState.NothingAvailable)
        }
    }

    private fun validateNewData(viewType: ViewType, categoryDto: CategoryDto): ViewState {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] validateNewData")
        return when {
            categoryDto.isError || categoryDto.items.isEmpty() -> {
                Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] error list")
                ViewState.NothingAvailable
            }

            else -> ViewState.FavoritesLoaded(categoryDto.items, viewType)
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object NothingAvailable : ViewState
        data class FavoritesLoaded(val items: List<CategoryItemDto>, val viewType: ViewType) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class SetViewType(val type: ViewType) : ViewAction
        data class SelectItem(val item: CategoryItemDto) : ViewAction
        data class Unfavorite(val item: CategoryItemDto) : ViewAction
        data object UnfavoriteSelected : ViewAction
        data object UndoRecentUnfavorite : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowUnfavoriteToast(val itemCount: Int) : ViewEffect
    }

}

