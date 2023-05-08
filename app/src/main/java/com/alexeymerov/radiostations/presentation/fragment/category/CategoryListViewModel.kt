package com.alexeymerov.radiostations.presentation.fragment.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.send
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState.NothingAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) : ViewModel() {

    private val _viewState = Channel<ViewState>(Channel.CONFLATED)
    val viewState = _viewState.receiveAsFlow()

    fun getCategories(categoryUrl: String) = categoryUseCase.getCategoriesByUrl(categoryUrl)
        .filter {
            if (it.isNotEmpty() && it[0].text == "No stations or shows available") {
                setNewState(NothingAvailable)
                return@filter false
            }
            return@filter true
        }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    private fun setNewState(state: ViewState) = _viewState.send(viewModelScope, state)

    sealed interface ViewState {
        object NothingAvailable : ViewState
    }

}

