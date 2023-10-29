package com.alexeymerov.radiostations.presentation.screen.favorite

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCase
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCase.ViewType
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoritesViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoritesViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoritesViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val categoryUseCase: CategoryUseCase,
    private val settingsUseCase: FavoriteViewSettingsUseCase
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    private lateinit var currentViewType: ViewType

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

    val categoriesFlow: StateFlow<List<CategoryItemDto>> = categoryUseCase
        .getFavorites()
        .catch { handleError(it) }
        .onEach(::validateNewData)
        .map { it.items }
        .flowOn(ioContext)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun nextViewType() {
        viewModelScope.launch(ioContext) {
            settingsUseCase.setNextViewType(currentViewType)
        }
    }

    override fun setAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new action: ${action.javaClass.simpleName}")
        super.setAction(action)
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] handleAction: ${action.javaClass.simpleName}")
        if (action is ViewAction.ToggleFavorite) {
            viewModelScope.launch(ioContext) {
                categoryUseCase.toggleFavorite(action.item)
            }
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
        data class ToggleFavorite(val item: CategoryItemDto) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

