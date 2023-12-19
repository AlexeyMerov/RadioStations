package com.alexeymerov.radiostations.feature.favorite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val audioUseCase: AudioUseCase,
    private val settingsUseCase: FavoriteViewSettingsUseCase
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    private lateinit var currentViewType: ViewType

    private val recentlyUnfavorited = mutableSetOf<CategoryItemDto>()
    private val selectedItems = mutableSetOf<CategoryItemDto>()
    var selectedItemsCount by mutableIntStateOf(0)

    private var undoJobTimeout: Job? = null

    init {
        viewModelScope.launch(ioContext) {
            settingsUseCase
                .getViewType()
                .filter { ::currentViewType.isInitialized }
                .filter { it != currentViewType }
                .collectLatest {
                    currentViewType = it
                    setState(ViewState.FavoritesLoaded(currentViewType))
                }
        }
    }

    val categoriesFlow: StateFlow<List<CategoryItemDto>> = audioUseCase.getFavorites()
        .catch { handleError(it) }
        .onEach(::validateNewData)
        .map { it.items }
        .flowOn(ioContext)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    override fun setAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new action: ${action.javaClass.simpleName}")
        super.setAction(action)
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] handleAction: ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.SetViewType -> setViewType(action.type)
            is ViewAction.SelectItem -> selectItem(action.item)
            is ViewAction.Unfavorite -> unfavorite(action.item)
            is ViewAction.UnfavoriteSelected -> unfavoriteSelected()
            is ViewAction.UndoRecentUnfavorite -> undoRecetUnfavorite()
        }
    }

    private fun undoRecetUnfavorite() {
        viewModelScope.launch(ioContext) {
            recentlyUnfavorited.forEach {
                audioUseCase.setFavorite(it)
            }
            recentlyUnfavorited.clear()
        }
    }

    private fun selectItem(item: CategoryItemDto) {
        viewModelScope.launch(ioContext) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                selectedItemsCount--
            } else {
                selectedItems.add(item)
                selectedItemsCount++
            }
        }
    }

    private fun setViewType(type: ViewType) {
        viewModelScope.launch(ioContext) {
            settingsUseCase.setViewType(type)
        }
    }

    private fun unfavorite(item: CategoryItemDto) {
        viewModelScope.launch(ioContext) {
            audioUseCase.unfavorite(item)
            recentlyUnfavorited.clear()
            recentlyUnfavorited.add(item)
            showUndoSnackbar()
        }
    }

    private fun unfavoriteSelected() {
        viewModelScope.launch(ioContext) {
            selectedItems.forEach {
                audioUseCase.unfavorite(it)
                recentlyUnfavorited.add(it)
            }
            selectedItemsCount = 0
            selectedItems.clear()
            showUndoSnackbar()
        }
    }

    private suspend fun showUndoSnackbar() {
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

    private fun validateNewData(categoryDto: CategoryDto) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] validateNewData")
        viewModelScope.launch(ioContext) {
            when {
                categoryDto.isError -> {
                    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] error list")
                    setState(ViewState.NothingAvailable)
                }

                categoryDto.items.isEmpty() -> {
                    setState(ViewState.NothingAvailable)
                }

                else -> {
                    try {
                        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] set FavoritesLoaded ${categoryDto.items.size}")
                        if (!::currentViewType.isInitialized) {
                            currentViewType = settingsUseCase.getViewType().first()
                        }
                        setState(ViewState.FavoritesLoaded(currentViewType))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object NothingAvailable : ViewState
        data class FavoritesLoaded(val viewType: ViewType) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class Unfavorite(val item: CategoryItemDto) : ViewAction
        data class SetViewType(val type: ViewType) : ViewAction
        data class SelectItem(val item: CategoryItemDto) : ViewAction
        data object UnfavoriteSelected : ViewAction
        data object UndoRecentUnfavorite : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowUnfavoriteToast(val itemCount: Int) : ViewEffect
    }

}

