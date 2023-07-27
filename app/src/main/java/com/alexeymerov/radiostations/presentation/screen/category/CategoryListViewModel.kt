package com.alexeymerov.radiostations.presentation.screen.category

import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
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

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    override fun createInitialState() = ViewState.Loading

    @OptIn(FlowPreview::class)
    override fun handleAction(action: ViewAction) {
        if (action is ViewAction.LoadCategories) {
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] handleAction: ${action.javaClass.simpleName}")
            viewModelScope.launch(Dispatchers.IO) {
                categoryUseCase.getCategoriesByUrl(action.url)
                    .timeout(15.toDuration(DurationUnit.SECONDS))
                    .catch {
                        if (viewState.value == ViewState.Loading) {
                            setState(ViewState.NothingAvailable)
                        }
                    }
                    .collectLatest {
                        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] getCategories: ${action.url}")
                        if (it.isError) {
                            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] error list")
                            setState(ViewState.NothingAvailable)
                        } else {
                            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] set CategoriesLoaded ${it.items.size}")
                            setState(ViewState.CategoriesLoaded(it.items))
                        }
                    }

            }
            categoryUseCase.loadCategoriesByUrl(action.url)
        }
    }

    /**
     * It's not a MVI but it used to be. At the moment not need to handle different states since there is one get and show the list.
     * */
    sealed interface ViewState : BaseViewState {
        object Loading : ViewState
        object NothingAvailable : ViewState
        data class CategoriesLoaded(val list: List<CategoryItemDto>) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        class LoadCategories(val url: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

