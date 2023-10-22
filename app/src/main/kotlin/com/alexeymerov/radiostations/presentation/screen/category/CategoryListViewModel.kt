package com.alexeymerov.radiostations.presentation.screen.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.presentation.navigation.NavDest
import com.alexeymerov.radiostations.presentation.navigation.decodeUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val categoryUseCase: CategoryUseCase) :
    BaseViewModel<CategoryListViewModel.ViewState, CategoryListViewModel.ViewAction, CategoryListViewModel.ViewEffect>() {

    private val categoryUrl = checkNotNull(savedStateHandle.get<String>(NavDest.Category.ARG_URL)).decodeUrl()

    private val headerFlow = MutableStateFlow(listOf<CategoryItemDto>())

    val categoriesFlow: StateFlow<List<CategoryItemDto>> = categoryUseCase
        .getCategoriesByUrl(categoryUrl)
        .catch { handleError() }
        .onEach(::prepareHeaders)
        .combine(headerFlow, ::filterCategories)
        .onEach(::validateNewData)
        .map { it.items }
        .flowOn(ioContext)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override fun setAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new action: ${action.javaClass.simpleName}")
        super.setAction(action)
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] handleAction: ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.LoadCategories -> loadCategories(categoryUrl)
            is ViewAction.FilterByHeader -> updateHeaderFlow(action.headerItem)
        }
    }

    private fun updateHeaderFlow(headerItem: CategoryItemDto) {
        headerFlow.update { headerList ->
            headerList.map { itemInFlow ->
                when (itemInFlow) {
                    headerItem -> itemInFlow.copy(isFiltered = !itemInFlow.isFiltered)
                    else -> itemInFlow
                }
            }
        }
    }

    private suspend fun prepareHeaders(categoryDto: CategoryDto) {
        val hasHeaders = categoryDto.items.any { it.type == DtoItemType.HEADER }
        if (headerFlow.value.isEmpty() && hasHeaders) {
            val headerList = categoryDto.items.filter { it.type == DtoItemType.HEADER }
            headerFlow.emit(headerList)
        }
    }

    private fun filterCategories(categoryDto: CategoryDto, headers: List<CategoryItemDto>): CategoryDto {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] categories combine")
        val items = categoryDto.items.toMutableList()

        if (headers.isNotEmpty()) {
            val resultList = mutableListOf<CategoryItemDto>()
            headers
                .filter { it.isFiltered }
                .forEach { header ->
                    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] header filtering: $header")
                    val headerPosition = items.indexOfFirst { it.url == header.url }
                    val listForHeader = items.subList(headerPosition, headerPosition + header.subItemsCount + 1)
                    resultList.addAll(listForHeader)
                }
            if (resultList.isNotEmpty()) {
                return categoryDto.copy(items = resultList)
            }
        }

        return categoryDto
    }

    private fun loadCategories(categoryUrl: String) {
        viewModelScope.launch(ioContext) {
            categoryUseCase.loadCategoriesByUrl(categoryUrl)
        }
    }

    private fun handleError() {
        if (viewState.value == ViewState.Loading) {
            setState(ViewState.NothingAvailable)
        }
    }

    private fun validateNewData(categoryDto: CategoryDto) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] validateNewData")
        viewModelScope.launch(ioContext) {
            if (categoryDto.isError) {
                Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] error list")
                setState(ViewState.NothingAvailable)
            } else {
                Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] set CategoriesLoaded ${categoryDto.items.size}")
                setState(ViewState.CategoriesLoaded(headerFlow.value))
            }
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object NothingAvailable : ViewState
        data class CategoriesLoaded(val headerItems: List<CategoryItemDto>) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data object LoadCategories : ViewAction
        class FilterByHeader(val headerItem: CategoryItemDto) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}

