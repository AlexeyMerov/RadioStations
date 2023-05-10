package com.alexeymerov.radiostations.presentation.fragment.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.send
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListViewModel.ViewState.NothingAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) : ViewModel() {

    /**
     * In my vision Channel is more handful then Stateflow. At least for current needs.
     * */
    private val _viewState = Channel<ViewState>(Channel.CONFLATED)
    val viewState = _viewState.receiveAsFlow()

    /**
     * Original intent was to implement it as MVI but for dynamic params it'll be a total mess with subscription to flow.
     * */
    fun getCategories(categoryUrl: String) = categoryUseCase.getCategoriesByUrl(categoryUrl)
        .filter(::filterCategories)
        .map { it.items }

    private fun filterCategories(it: CategoryDto): Boolean {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] check for empty list")
        if (it.isError) {
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] list is empty")
            setNewState(NothingAvailable)
            return false
        }
        return true
    }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    private fun setNewState(state: ViewState) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new state $state")
        _viewState.send(viewModelScope, state)
    }

    /**
     * It's not a MVI but it used to be. At the moment not need to handle different states since there is one get and show the list.
     * */
    sealed interface ViewState {
        object NothingAvailable : ViewState
    }

}

