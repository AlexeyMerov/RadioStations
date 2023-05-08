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
        .filter {
            Timber.d("check for empty list")
            if (it.isNotEmpty() && it[0].text == "No stations or shows available") { //todo remove hardcode or at leas move to lower levels
                Timber.d("list is empty")
                setNewState(NothingAvailable)
                return@filter false
            }
            return@filter true
        }

    override fun onCleared() {
        super.onCleared()
        categoryUseCase.cancelJobs()
    }

    private fun setNewState(state: ViewState) {
        Timber.d("new state $state")
        _viewState.send(viewModelScope, state)
    }

    /**
     * It's not a MVI but it used to be. At the moment not need to handle different states since there is one get and show the list.
     * */
    sealed interface ViewState {
        object NothingAvailable : ViewState
    }

}

