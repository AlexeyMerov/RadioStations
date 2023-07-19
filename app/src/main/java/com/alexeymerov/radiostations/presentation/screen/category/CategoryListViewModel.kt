package com.alexeymerov.radiostations.presentation.screen.category

import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
        .onEach { list ->
            if (list.isNotEmpty()) setState(ViewState.CategoriesLoaded)
        }

    private fun filterCategories(it: CategoryDto): Boolean {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] check for error list: ${it.isError}")
        if (it.isError) {
            setState(ViewState.NothingAvailable)
            return false
        }
        return true
    }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        if (action is ViewAction.LoadCategories) {
            categoryUseCase.loadCategoriesByUrl(action.url)
        }
    }

    /**
     * It's not a MVI but it used to be. At the moment not need to handle different states since there is one get and show the list.
     * */
    sealed interface ViewState : BaseViewState {
        object Loading : ViewState
        object NothingAvailable : ViewState
        object CategoriesLoaded : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        class LoadCategories(val url: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

