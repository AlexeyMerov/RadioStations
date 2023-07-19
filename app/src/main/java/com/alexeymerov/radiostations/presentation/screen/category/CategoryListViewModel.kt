package com.alexeymerov.radiostations.presentation.screen.category

import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.presentation.BaseViewAction
import com.alexeymerov.radiostations.presentation.BaseViewEffect
import com.alexeymerov.radiostations.presentation.BaseViewModel
import com.alexeymerov.radiostations.presentation.BaseViewState
import com.alexeymerov.radiostations.presentation.screen.category.CategoryListViewModel.ViewState.NothingAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) :
    BaseViewModel<CategoryListViewModel.ViewState, CategoryListViewModel.ViewAction, CategoryListViewModel.ViewEffect>() {

    /**
     * Original intent was to implement it as MVI but for dynamic params it'll be a total mess with subscription to flow.
     * */
    fun getCategories(categoryUrl: String) = categoryUseCase.getCategoriesByUrl(categoryUrl)
        .filter(::filterCategories)
        .map { it.items }
        .distinctUntilChanged()

    private fun filterCategories(it: CategoryDto): Boolean {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] check for empty list")
        if (it.isError) {
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] list is empty")
            setState(NothingAvailable)
            return false
        }
        return true
    }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    override fun createInitialState() = ViewState.Ignore

    override fun handleAction(action: ViewAction) {
        if (action is ViewAction.LoadCategories) {
            categoryUseCase.loadCategoriesByUrl(action.url)
        }
    }

    /**
     * It's not a MVI but it used to be. At the moment not need to handle different states since there is one get and show the list.
     * */
    sealed interface ViewState : BaseViewState {
        object Ignore : ViewState
        object NothingAvailable : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        class LoadCategories(val url: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

