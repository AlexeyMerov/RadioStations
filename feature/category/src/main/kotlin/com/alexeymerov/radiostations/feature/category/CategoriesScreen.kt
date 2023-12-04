package com.alexeymerov.radiostations.feature.category

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isTablet
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.core.ui.view.ErrorView
import com.alexeymerov.radiostations.core.ui.view.ShimmerLoading
import com.alexeymerov.radiostations.core.ui.view.StationListItem
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewAction
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewState
import com.alexeymerov.radiostations.feature.category.item.CategoryListItem
import com.alexeymerov.radiostations.feature.category.item.HeaderListItem
import com.alexeymerov.radiostations.feature.category.item.SubCategoryListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseCategoryScreen(
    viewModel: CategoriesViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    defTitle: String,
    categoryTitle: String,
    parentRoute: String,
    onNavigate: (String) -> Unit
) {
    ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    if (isVisibleToUser) TopBarSetup(categoryTitle, defTitle, topBarBlock)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }
    LaunchedEffect(Unit) { viewModel.setAction(ViewAction.LoadCategories) }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.categoriesFlow.collectAsStateWithLifecycle()

    val refreshing by viewModel.isRefreshing
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.setAction(ViewAction.UpdateCategories) }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        CategoryScreen(
            viewState = viewState,
            categoryItems = categoryItems,
            parentRoute = parentRoute,
            onNavigate = onNavigate,
            onAction = { viewModel.setAction(it) }
        )

        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun TopBarSetup(
    categoryTitle: String,
    defTitle: String,
    topBarBlock: (TopBarState) -> Unit
) {
    val displayBackButton by rememberSaveable(categoryTitle) { mutableStateOf(categoryTitle != defTitle) }
    LaunchedEffect(Unit, displayBackButton) {
        topBarBlock.invoke(TopBarState(title = categoryTitle, displayBackButton = displayBackButton))
    }
}

@Composable
private fun CategoryScreen(
    viewState: ViewState,
    categoryItems: Map<CategoryItemDto?, List<CategoryItemDto>>,
    parentRoute: String,
    onNavigate: (String) -> Unit,
    onAction: (ViewAction) -> Unit
) {
    val config = LocalConfiguration.current
    when (viewState) {
        is ViewState.NothingAvailable -> ErrorView()
        is ViewState.Loading -> ShimmerLoading()
        is ViewState.CategoriesLoaded -> {
            MainContent(
                categoryItems = categoryItems,
                headerItems = viewState.headerItems,
                onHeaderFilterClick = { onAction.invoke(ViewAction.FilterByHeader(it)) },
                onCategoryClick = {
                    onNavigate.invoke(Screens.Categories.createRoute(it.text, it.url))
                },
                onAudioClick = {
                    val route = Screens.Player(parentRoute).createRoute(
                        stationName = it.text,
                        locationName = it.subText.orEmpty(),
                        stationImgUrl = it.image.orEmpty(),
                        rawUrl = it.url,
                        id = it.id,
                        isFav = it.isFavorite
                    )
                    onNavigate.invoke(route)
                },
                onFavClick = { onAction.invoke(ViewAction.ToggleFavorite(it)) }
            )
        }
    }
}

@Composable
private fun MainContent(
    categoryItems: Map<CategoryItemDto?, List<CategoryItemDto>>,
    headerItems: List<CategoryItemDto>,
    onHeaderFilterClick: (CategoryItemDto) -> Unit,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val config = LocalConfiguration.current

    val columnCount = remember(config) {
        var count = 1
        if (config.isTablet()) count += 1
        if (config.isLandscape()) count += 1
        return@remember count
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
    ) {
        if (headerItems.isNotEmpty()) filtersHeader(headerItems, onHeaderFilterClick)

        categoryItems.forEach { (header, items) ->
            if (header != null) stickyHeader(header, coroutineScope, listState)
            if (columnCount > 1) {
                mainGridItems(header, config, columnCount, items, onCategoryClick, onAudioClick, onFavClick)
            } else {
                mainListItems(items, onCategoryClick, onAudioClick, onFavClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun LazyListScope.filtersHeader(
    headerItems: List<CategoryItemDto>,
    onHeaderFilterClick: (CategoryItemDto) -> Unit
) {
    item(key = "TopHeader", contentType = "Filters") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            headerItems.forEach { item ->
                FilterChip(
                    onClick = { onHeaderFilterClick.invoke(item) },
                    label = { Text(text = item.text) },
                    selected = item.isFiltered
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.stickyHeader(
    header: CategoryItemDto,
    coroutineScope: CoroutineScope,
    listState: LazyListState
) {
    stickyHeader(
        key = header.id,
        contentType = header.id + "_header"
    ) {
        HeaderListItem(
            modifier = Modifier
                .animateItemPlacement()
                .fillMaxWidth(),
            itemDto = header,
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(header.absoluteIndex)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.mainListItems(
    items: List<CategoryItemDto>,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    items(
        items = items,
        key = CategoryItemDto::id,
        contentType = CategoryItemDto::type
    ) { itemDto ->
        val defaultModifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateItemPlacement()

        when (itemDto.type) {
            DtoItemType.CATEGORY -> CategoryListItem(defaultModifier, itemDto, onCategoryClick)
            DtoItemType.AUDIO -> StationListItem(
                modifier = defaultModifier,
                itemDto = itemDto,
                inSelection = false,
                isSelected = false,
                onAudioClick = onAudioClick,
                onFavClick = onFavClick
            )

            DtoItemType.SUBCATEGORY -> SubCategoryListItem(defaultModifier, itemDto, onCategoryClick)
            DtoItemType.HEADER -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.mainGridItems(
    header: CategoryItemDto?,
    config: Configuration,
    columnCount: Int,
    items: List<CategoryItemDto>,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    item(
        key = header?.id + "_content",
        contentType = "CategoryContent"
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .animateItemPlacement()
                .heightIn(max = config.screenHeightDp.dp),
            columns = GridCells.Fixed(columnCount),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = items,
                key = CategoryItemDto::id,
                contentType = CategoryItemDto::type
            ) { itemDto ->
                val defaultModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .animateItemPlacement()

                when (itemDto.type) {
                    DtoItemType.CATEGORY -> CategoryListItem(defaultModifier, itemDto, onCategoryClick)
                    DtoItemType.AUDIO -> StationListItem(
                        modifier = defaultModifier,
                        itemDto = itemDto,
                        inSelection = false,
                        isSelected = false,
                        onAudioClick = onAudioClick,
                        onFavClick = onFavClick
                    )

                    DtoItemType.SUBCATEGORY -> SubCategoryListItem(defaultModifier, itemDto, onCategoryClick)
                    DtoItemType.HEADER -> {}
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainContentPreview() {
    val categoryItems: List<CategoryItemDto> = listOf(
        CategoryItemDto("url#1", "", text = "Header", type = DtoItemType.HEADER, subItemsCount = 1, initials = "G"),
        CategoryItemDto("url#2", "", text = "Category", type = DtoItemType.CATEGORY, initials = "G"),
        CategoryItemDto("url#3", "", text = "Very Long Category Name", type = DtoItemType.CATEGORY, initials = "G"),
        CategoryItemDto("url#4", "", text = "Another Header", type = DtoItemType.HEADER, subItemsCount = 22, initials = "G"),
        CategoryItemDto("url#5", "", text = "Subcategory", type = DtoItemType.SUBCATEGORY, initials = "G"),
        CategoryItemDto("url#6", "", text = "Long Subcategory", type = DtoItemType.SUBCATEGORY, initials = "G"),
        CategoryItemDto("url#7", "", text = "Station (City)", type = DtoItemType.AUDIO, initials = "G"),
        CategoryItemDto("url#8", "", text = "Station", type = DtoItemType.AUDIO, initials = "G"),
    )
    val headers = listOf(
        CategoryItemDto("url#1", "", text = "Header", type = DtoItemType.HEADER, isFiltered = true, subItemsCount = 1, initials = "G"),
        CategoryItemDto("url#1", "", text = "Long Header", type = DtoItemType.HEADER, subItemsCount = 22, initials = "G"),
        CategoryItemDto(
            id = "url#3",
            url = "",
            text = "Very Long Header",
            type = DtoItemType.HEADER,
            isFiltered = true,
            subItemsCount = 999,
            initials = "G"
        ),
        CategoryItemDto("url#4", "", text = "Tiny", type = DtoItemType.HEADER, initials = "G"),
        CategoryItemDto("url#5", "", text = "Header", type = DtoItemType.HEADER, isFiltered = true, initials = "G"),
    )
    MainContent(mapOf(null to categoryItems), headers, {}, {}, {}, {})
}