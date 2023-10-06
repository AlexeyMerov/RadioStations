package com.alexeymerov.radiostations.presentation.screen.category

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class CategoryListViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) :
    BaseViewModel<CategoryListViewModel.ViewState, CategoryListViewModel.ViewAction, CategoryListViewModel.ViewEffect>() {

    override fun setAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new action: ${action.javaClass.simpleName}")
        super.setAction(action)
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] handleAction: ${action.javaClass.simpleName}")
        if (action is ViewAction.LoadCategories) {
            processLoadCategories(action.url)
        }
    }

    private fun processLoadCategories(categoryUrl: String) {
        subscribeToCategoriesFlow(categoryUrl)
        loadCategories(categoryUrl)
    }

    @OptIn(FlowPreview::class)
    private fun subscribeToCategoriesFlow(categoryUrl: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            categoryUseCase.getCategoriesByUrl(categoryUrl)
                .timeout(15.toDuration(DurationUnit.SECONDS))
                .catch { handleError() }
                .collectLatest {
                    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] collectLatest: $categoryUrl")
                    processNewData(it)
                }
        }
    }

    private fun loadCategories(categoryUrl: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            categoryUseCase.loadCategoriesByUrl(categoryUrl)
        }
    }

    private fun handleError() {
        if (viewState.value == ViewState.Loading) {
            setState(ViewState.NothingAvailable)
        }
    }

    private fun processNewData(categoryDto: CategoryDto) {
        if (categoryDto.isError) {
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] error list")
            setState(ViewState.NothingAvailable)
        } else {
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] set CategoriesLoaded ${categoryDto.items.size}")
            setState(ViewState.CategoriesLoaded(categoryDto.items))
        }
    }

    /**
     * It's not a MVI but it used to be. At the moment not need to handle different states since there is one get and show the list.
     * */
    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object NothingAvailable : ViewState
        data class CategoriesLoaded(val list: List<CategoryItemDto>) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        class LoadCategories(val url: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

