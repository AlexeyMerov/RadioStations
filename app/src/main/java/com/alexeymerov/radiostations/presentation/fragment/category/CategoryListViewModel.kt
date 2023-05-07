package com.alexeymerov.radiostations.presentation.fragment.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.send
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState.CategoriesLoaded
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState.NothingAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) : ViewModel() {

    private val _viewState = Channel<ViewState>(Channel.CONFLATED)
    val viewState = _viewState.receiveAsFlow()

    private fun loadCategories(categoryUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("CategoryListViewModel -> loadCategories")
            val categories = categoryUseCase.getCategoriesByUrl(categoryUrl)
            if (categories.first().text == "No stations or shows available") {
                setNewState(NothingAvailable)
            } else {
                setNewState(CategoriesLoaded(categories))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    private fun setNewState(state: ViewState) = _viewState.send(viewModelScope, state)

    fun processAction(action: ViewAction) {
        Timber.d("New action: " + action.javaClass.simpleName)
        if (action is ViewAction.LoadCategories) loadCategories(action.categoryUrl)
    }

    sealed interface ViewState {
        object NothingAvailable : ViewState
        class CategoriesLoaded(val categories: List<CategoryEntity>) : ViewState
    }

    sealed interface ViewAction {
        class LoadCategories(val categoryUrl: String) : ViewAction
    }
}

