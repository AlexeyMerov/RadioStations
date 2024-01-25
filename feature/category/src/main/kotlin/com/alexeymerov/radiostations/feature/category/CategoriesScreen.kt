package com.alexeymerov.radiostations.feature.category

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.common.LocalConnectionStatus
import com.alexeymerov.radiostations.core.ui.common.LocalDarkMode
import com.alexeymerov.radiostations.core.ui.common.LocalNightMode
import com.alexeymerov.radiostations.core.ui.common.LocalPlayerVisibility
import com.alexeymerov.radiostations.core.ui.common.LocalSnackbar
import com.alexeymerov.radiostations.core.ui.extensions.defListItemHeight
import com.alexeymerov.radiostations.core.ui.extensions.defListItemModifier
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
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
    val isNetworkAvailable = LocalConnectionStatus.current

    Box(
        Modifier
            .fillMaxSize()
            .run { if (isNetworkAvailable) pullRefresh(pullRefreshState) else this }
    ) {
        CategoryScreen(
            viewState = viewState,
            categoryItems = categoryItems,
            parentRoute = parentRoute,
            onNavigate = onNavigate,
            onAction = { viewModel.setAction(it) }
        )

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = refreshing,
            state = pullRefreshState
        )
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
    categoryItems: List<HeaderWithItems>,
    parentRoute: String,
    onNavigate: (String) -> Unit,
    onAction: (ViewAction) -> Unit
) {
    val isNetworkAvailable = LocalConnectionStatus.current
    when (viewState) {
        is ViewState.NothingAvailable -> ErrorView()
        is ViewState.Loading -> ShimmerLoading(defListItemModifier)
        is ViewState.CategoriesLoaded -> {
            MainContent(
                categoryItems = categoryItems,
                filterHeaderItems = viewState.filterHeaderItems,
                onHeaderFilterClick = { onAction.invoke(ViewAction.FilterByHeader(it)) },
                onCategoryClick = {
                    onNavigate.invoke(Screens.Categories.createRoute(it.text, it.url))
                },
                onAudioClick = {
                    if (isNetworkAvailable) {
                        val route = Screens.Player(parentRoute).createRoute(
                            stationName = it.text,
                            locationName = it.subText.orEmpty(),
                            stationImgUrl = it.image.orEmpty(),
                            rawUrl = it.url,
                            id = it.id,
                            isFav = it.isFavorite
                        )
                        onNavigate.invoke(route)
                    }
                },
                onFavClick = { onAction.invoke(ViewAction.ToggleFavorite(it)) }
            )
        }
    }
}

// It works but...
// Idea was to fetch locations by name and show with custom pins with station images.
// Geocoder works not as fast as i hoped and BitmapDecoder from Coil also looks not right.
// Later will try to find some REST apis to preload needed data in advance and not in place.
@Composable
private fun ShowMap() {
    val isDarkMode = LocalDarkMode.current
    val isNightMode = LocalNightMode.current
    val context = LocalContext.current

    val mapStyle = when {
        isDarkMode -> MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        isNightMode -> MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_night)
        else -> null
    }

    val location = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 5f)

    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapStyleOptions = mapStyle)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    categoryItems: List<HeaderWithItems>,
    filterHeaderItems: List<CategoryItemDto>,
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

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            userScrollEnabled = listState.canScrollForward || listState.canScrollBackward,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 50.dp, top = 4.dp)
        ) {
            if (filterHeaderItems.isNotEmpty()) filtersHeader(filterHeaderItems, onHeaderFilterClick)

            categoryItems.forEachIndexed { index, (header, items) ->
                if (header != null) {
                    stickyHeader(
                        header = header,
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(header.absoluteIndex)
                            }
                        }
                    )
                } else if (index > 0) {
                    // needs to unstick previous header
                    stickyHeader(
                        key = index,
                        contentType = "emptyHeader"
                    ) {
                        Spacer(Modifier)
                    }
                }

                if (columnCount > 1) {
                    mainGridItems(header, columnCount, items, onCategoryClick, onAudioClick, onFavClick)
                } else {
                    mainListItems(items, onCategoryClick, onAudioClick, onFavClick)
                }
            }
        }

        val bottomPadding by animateDpAsState(targetValue = if (LocalPlayerVisibility.current) 64.dp else 16.dp, label = String.EMPTY)
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = bottomPadding,
                    end = 16.dp
                ),
            visible = listState.canScrollBackward,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                content = { Icon(Icons.Rounded.ArrowUpward, null) }
            )
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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
    onClick: () -> Unit
) {
    stickyHeader(
        key = header.id,
        contentType = "header"
    ) {
        HeaderListItem(
            modifier = Modifier
                .animateItemPlacement()
                .fillMaxWidth(),
            itemDto = header,
            onClick = { onClick.invoke() }
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
        DrawItems(
            modifier = defListItemModifier.animateItemPlacement(),
            itemDto = itemDto,
            onCategoryClick = onCategoryClick,
            onAudioClick = onAudioClick,
            onFavClick = onFavClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.mainGridItems(
    header: CategoryItemDto?,
    columnCount: Int,
    items: List<CategoryItemDto>,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    val columnHeight = defListItemHeight * (items.size / columnCount).coerceAtLeast(1)
    item(
        key = header?.id + "_content",
        contentType = "CategoryContent"
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .animateItemPlacement()
                .heightIn(max = columnHeight),
            columns = GridCells.Fixed(columnCount),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = items,
                key = CategoryItemDto::id,
                contentType = CategoryItemDto::type
            ) { itemDto ->
                DrawItems(
                    modifier = defListItemModifier.animateItemPlacement(),
                    itemDto = itemDto,
                    onCategoryClick = onCategoryClick,
                    onAudioClick = onAudioClick,
                    onFavClick = onFavClick
                )
            }
        }
    }
}

@Composable
fun DrawItems(
    modifier: Modifier,
    itemDto: CategoryItemDto,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    val snackbar = LocalSnackbar.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    when (itemDto.type) {
        DtoItemType.CATEGORY -> CategoryListItem(
            modifier = modifier,
            itemDto = itemDto,
            onCategoryClick = onCategoryClick,
            onRevealAction = {
                coroutineScope.launch {
                    val showSnackbar = snackbar.showSnackbar(
                        message = "Snackbar message",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (showSnackbar == SnackbarResult.ActionPerformed) {
                        Toast.makeText(context, "Undo clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        DtoItemType.AUDIO -> StationListItem(
            modifier = modifier,
            itemDto = itemDto,
            inSelection = false,
            isSelected = false,
            onAudioClick = onAudioClick,
            onFavClick = onFavClick
        )

        DtoItemType.SUBCATEGORY -> SubCategoryListItem(modifier, itemDto, onCategoryClick)
        DtoItemType.HEADER -> {}
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
    MainContent(listOf(HeaderWithItems(items = categoryItems)), headers, {}, {}, {}, {})
}