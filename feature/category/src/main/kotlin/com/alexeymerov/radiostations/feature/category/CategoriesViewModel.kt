package com.alexeymerov.radiostations.feature.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.analytics.AnalyticsEvents
import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase,
    private val audioUseCase: AudioUseCase,
    private val dispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
    analytics: FirebaseAnalytics
) : BaseViewModel<CategoriesViewModel.ViewState, CategoriesViewModel.ViewAction, CategoriesViewModel.ViewEffect>() {

    private val categoryUrl = checkNotNull(savedStateHandle.get<String>(Screens.Categories.Const.ARG_URL)).decodeUrl()
    private val categoryTitle = checkNotNull(savedStateHandle.get<String>(Screens.Categories.Const.ARG_TITLE)).decodeUrl()

    var isRefreshing = MutableStateFlow(false)

    private val allItemsFlow = categoryUseCase.getAllByUrl(categoryUrl)
        .catch { handleError(it) }
        .filter(::isDataValid)
        .map { it.items }
        .onEach(::prepareFilterHeaders)

    private val filterHeaderFlow = MutableStateFlow(emptyList<CategoryItemDto>())

    private val filteredItemsFlow = allItemsFlow
        .combine(filterHeaderFlow, ::filterCategoriesByHeader)
        .flowOn(dispatcher)

    private val categoryFlow = filteredItemsFlow
        .map(::mapListToHeadersWithItems)
        .flowOn(dispatcher)

    private val itemsWithLocationFlow = filteredItemsFlow
        .map(::prepareMapItems)
        .flowOn(dispatcher)

    init {
        analytics.logEvent(AnalyticsEvents.LOAD_CATEGORY) {
            param(AnalyticsParams.TITLE, categoryTitle)
        }

        viewModelScope.launch(dispatcher) {
            allItemsFlow.collectLatest {
                Timber.d("CategoriesViewModel collectLatest")

                val state = ViewState.CategoriesLoaded(
                    categoryItems = categoryFlow,
                    filterHeaderItems = filterHeaderFlow,
                    itemsWithLocation = itemsWithLocationFlow
                )

                setState(state)
            }
        }
    }

    override fun setAction(action: ViewAction) {
        Timber.d("setAction: ${action.javaClass.simpleName}")
        super.setAction(action)
    }

    override fun createInitialState() = ViewState.Loading

    override fun handleAction(action: ViewAction) {
        Timber.d("handleAction: ${action.javaClass.simpleName}")
        when (action) {
            is ViewAction.LoadCategories -> loadCategories(categoryUrl)
            is ViewAction.FilterByHeader -> updateHeaderFlow(action.headerItem)
            is ViewAction.ToggleFavorite -> toggleFavorite(action)
            is ViewAction.UpdateCategories -> updateCategories(categoryUrl)
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "handleError")
        isRefreshing.value = false
        if (viewState.value == ViewState.Loading) {
            setState(ViewState.NothingAvailable, delay = 3000)
        }
    }

    private fun toggleFavorite(action: ViewAction.ToggleFavorite) {
        viewModelScope.launch(dispatcher) {
            audioUseCase.toggleFavorite(action.item)
        }
    }

    private fun updateHeaderFlow(headerItem: CategoryItemDto) {
        filterHeaderFlow.update { headerList ->
            headerList.map { itemInFlow ->
                when (itemInFlow) {
                    headerItem -> itemInFlow.copy(isFiltered = !itemInFlow.isFiltered)
                    else -> itemInFlow
                }
            }
        }
    }

    /**
     * Extracting headers for filter chips and saving them separately
     * */
    private suspend fun prepareFilterHeaders(items: List<CategoryItemDto>) {
        if (filterHeaderFlow.value.isEmpty()) {
            val headerList = items.filter { it.type == DtoItemType.HEADER }
            if (headerList.isNotEmpty()) {
                filterHeaderFlow.emit(headerList)
            }
        }
    }

    /**
     * If filter chips were selected, then show only items for selected headers
     * */
    private fun filterCategoriesByHeader(items: List<CategoryItemDto>, headers: List<CategoryItemDto>): List<CategoryItemDto> {
        Timber.d("filterCategoriesByHeader")

        if (headers.isNotEmpty()) {
            val resultList = mutableListOf<CategoryItemDto>()

            headers
                .filter { it.isFiltered }
                .forEach { header ->
                    Timber.d("filterCategoriesByHeader: $header")
                    val headerPosition = items.indexOfFirst { it.url == header.url }
                    val listForHeader = items.subList(headerPosition, headerPosition + header.subItemsCount + 1)
                    resultList.addAll(listForHeader)
                }

            if (resultList.isNotEmpty()) {
                return resultList
            }
        }

        return items
    }

    private fun loadCategories(categoryUrl: String) {
        viewModelScope.launch(dispatcher) {
            categoryUseCase.loadCategoriesByUrl(categoryUrl)

            delay(10_000)
            if (viewState.value == ViewState.Loading) {
                setState(ViewState.NothingAvailable)
            }
        }
    }

    private fun updateCategories(categoryUrl: String) {
        viewModelScope.launch(dispatcher) {
            isRefreshing.value = true
            categoryUseCase.loadCategoriesByUrl(categoryUrl)

            delay(10_000)
            if (isRefreshing.value) isRefreshing.value = false
            if (viewState.value == ViewState.Loading) {
                setState(ViewState.NothingAvailable)
            }
        }
    }

    /**
     * Stop refreshing state
     * Check if there is no error and items are exist
     * */
    private fun isDataValid(categoryDto: CategoryDto): Boolean {
        Timber.d("isDataValid $categoryDto")
        isRefreshing.value = false

        val isError = categoryDto.isError
        val isEmpty = categoryDto.items.isEmpty()
        if (isError || isEmpty) {
            Timber.d("categoryDto isError = $isError, isEmpty = $isEmpty")

            if (viewState.value != ViewState.Loading) {
                setState(ViewState.NothingAvailable)
            }

            return false
        }

        return true
    }

    /**
     * We need to save same order for elements but separate header for sticky view
     * */
    private fun mapListToHeadersWithItems(items: List<CategoryItemDto>): List<HeaderWithItems> {
        val resultList = mutableListOf<HeaderWithItems>()

        // check if need to process header filtering at all
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

    /**
     * Filter all items without Lat Lng and calculating bounds to position/zoom pins on the map
     * For Top 40 category there are cases when Stations based in North America and Europe, so map would be shown in the middle of the ocean
     * */
    private fun prepareMapItems(items: List<CategoryItemDto>): Pair<LatLngBounds, List<CategoryItemDto>>? {
        val isLocationExist = items.any { it.hasLocation() }
        if (!isLocationExist) return null

        val boundsBuilder = LatLngBounds.builder()

        val filteredList = items.filter {
            val latitude = it.latitude
            val longitude = it.longitude

            if (latitude != null && longitude != null) {
                boundsBuilder.include(LatLng(latitude, longitude))
                return@filter true
            }

            return@filter false
        }

        return if (filteredList.isEmpty()) null else {
            val bounds = boundsBuilder.build()
            bounds to filteredList
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object NothingAvailable : ViewState

        data class CategoriesLoaded(
            val categoryItems: Flow<List<HeaderWithItems>>,
            val filterHeaderItems: StateFlow<List<CategoryItemDto>?>,
            val itemsWithLocation: Flow<Pair<LatLngBounds, List<CategoryItemDto>>?>
        ) : ViewState
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

data class HeaderWithItems(
    val header: CategoryItemDto? = null,
    val items: List<CategoryItemDto>
)

