package com.alexeymerov.radiostations.feature.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.domain.usecase.category.CategoryUseCase
import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.decodeUrl
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
class CategoriesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryUseCase: CategoryUseCase,
    private val audioUseCase: AudioUseCase,
    analytics: FirebaseAnalytics
) : BaseViewModel<CategoriesViewModel.ViewState, CategoriesViewModel.ViewAction, CategoriesViewModel.ViewEffect>() {

    private val categoryUrl = checkNotNull(savedStateHandle.get<String>(Screens.Categories.Const.ARG_URL)).decodeUrl()
    private val categoryTitle = checkNotNull(savedStateHandle.get<String>(Screens.Categories.Const.ARG_TITLE)).decodeUrl()

    private val headerFlow = MutableStateFlow(listOf<CategoryItemDto>())

    var isRefreshing = mutableStateOf(false)

    internal val categoriesFlow = categoryUseCase
        .getAllByUrl(categoryUrl)
        .catch { handleError(it) }
        .onEach(::prepareHeaders)
        .combine(headerFlow, ::filterCategories)
        .onEach(::validateNewData)
        .map { it.items }
        .map(::createHeaderWithItems)
        .flowOn(ioContext)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        analytics.logEvent("load_category") {
            param("title", categoryTitle)
        }
    }

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
            is ViewAction.ToggleFavorite -> toggleFavorite(action)
            is ViewAction.UpdateCategories -> updateCategories(categoryUrl)
        }
    }

    private fun toggleFavorite(action: ViewAction.ToggleFavorite) {
        viewModelScope.launch(ioContext) {
            audioUseCase.toggleFavorite(action.item)
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

    private fun updateCategories(categoryUrl: String) {
        viewModelScope.launch(ioContext) {
            isRefreshing.value = true
            categoryUseCase.loadCategoriesByUrl(categoryUrl)

            delay(10_000) // just random
            if (isRefreshing.value) isRefreshing.value = false
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "[ ${object {}.javaClass.enclosingMethod?.name} ] handleError")
        isRefreshing.value = false
        if (viewState.value == ViewState.Loading) {
            setState(ViewState.NothingAvailable)
        }
    }

    private fun validateNewData(categoryDto: CategoryDto) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] validateNewData")
        viewModelScope.launch(ioContext) {
            isRefreshing.value = false
            when {
                categoryDto.isError -> {
                    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] categoryDto.isError")
                    setState(ViewState.NothingAvailable)
                }

                categoryDto.items.isEmpty() -> {
                    if (viewState.value != ViewState.Loading) {
                        setState(ViewState.NothingAvailable)
                    } else {
                        setState(ViewState.NothingAvailable, delay = 7000)
                    }
                }

                else -> {
                    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] set CategoriesLoaded ${categoryDto.items.size}")
                    val headerValue = if (headerFlow.value.size > 1) headerFlow.value else emptyList()
                    setState(ViewState.CategoriesLoaded(headerValue))
                }
            }
        }
    }

    /**
     * We need to save same order for elements but separate header for sticky view
     * */
    private fun createHeaderWithItems(items: List<CategoryItemDto>): List<HeaderWithItems> {
        val resultList = mutableListOf<HeaderWithItems>()

        // if need to process header filtering at all
        if (items.any { it.type == DtoItemType.HEADER }) {

            var index = 0
            var itemsWithoutHeaders: MutableList<CategoryItemDto>? = null

            while (index < items.size) {
                val item = items[index]
                item.absoluteIndex = index

                if (item.type == DtoItemType.HEADER) {
                    // if we have some items without header then save it and clear variable
                    if (itemsWithoutHeaders != null) {
                        resultList.add(HeaderWithItems(items = itemsWithoutHeaders))
                        itemsWithoutHeaders = null
                    }

                    // save items for the current header based on item count from the source
                    val fromIndex = index + 1
                    val toIndex = index + item.subItemsCount + 1

                    resultList.add(
                        HeaderWithItems(
                            header = item,
                            items = items.subList(fromIndex, toIndex)
                        )
                    )

                    index = toIndex
                } else /* non header item */ {
                    if (itemsWithoutHeaders == null) itemsWithoutHeaders = mutableListOf()
                    itemsWithoutHeaders.add(item)
                    index++
                }
            }

            // if source list is ended but we still have some items without header
            if (itemsWithoutHeaders != null) {
                resultList.add(HeaderWithItems(items = itemsWithoutHeaders))
            }

        } else /* no headers at all */ {
            resultList.add(HeaderWithItems(items = items))
        }

        return resultList
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object NothingAvailable : ViewState
        data class CategoriesLoaded(val filterHeaderItems: List<CategoryItemDto>) : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data object LoadCategories : ViewAction
        data object UpdateCategories : ViewAction
        data class ToggleFavorite(val item: CategoryItemDto) : ViewAction
        data class FilterByHeader(val headerItem: CategoryItemDto) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        data class ShowToast(val text: String) : ViewEffect
    }

}

internal data class HeaderWithItems(
    val header: CategoryItemDto? = null,
    val items: List<CategoryItemDto>
)

