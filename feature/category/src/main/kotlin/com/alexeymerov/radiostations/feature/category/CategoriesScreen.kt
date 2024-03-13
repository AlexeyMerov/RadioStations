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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.common.LocalConnectionStatus
import com.alexeymerov.radiostations.core.ui.common.LocalPlayerVisibility
import com.alexeymerov.radiostations.core.ui.common.LocalSnackbar
import com.alexeymerov.radiostations.core.ui.common.LocalTopbar
import com.alexeymerov.radiostations.core.ui.common.TopBarState
import com.alexeymerov.radiostations.core.ui.extensions.defListItem
import com.alexeymerov.radiostations.core.ui.extensions.defListItemHeight
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isTablet
import com.alexeymerov.radiostations.core.ui.extensions.setIf
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.core.ui.view.ErrorView
import com.alexeymerov.radiostations.core.ui.view.ShimmerLoading
import com.alexeymerov.radiostations.core.ui.view.StationListItem
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewAction
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewState
import com.alexeymerov.radiostations.feature.category.elements.ShowMap
import com.alexeymerov.radiostations.feature.category.item.CategoryListItem
import com.alexeymerov.radiostations.feature.category.item.CategoryScreenTestTags
import com.alexeymerov.radiostations.feature.category.item.CategoryScreenTestTags.LAZY_LIST
import com.alexeymerov.radiostations.feature.category.item.CategoryScreenTestTags.SCROLL_TOP_FAB
import com.alexeymerov.radiostations.feature.category.item.HeaderListItem
import com.alexeymerov.radiostations.feature.category.item.SubCategoryListItem
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseCategoryScreen(
    viewModel: CategoriesViewModel,
    isVisibleToUser: Boolean,
    defTitle: String,
    categoryTitle: String,
    onNavigate: (String) -> Unit
) {
    ComposedTimberD("BaseCategoryScreen")
    val isNetworkAvailable = LocalConnectionStatus.current

    if (isVisibleToUser) TopBarSetup(categoryTitle, defTitle)

    LaunchedEffect(Unit) { viewModel.setAction(ViewAction.LoadCategories) }

    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.setAction(ViewAction.UpdateCategories) }
    )

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    Box(
        Modifier
            .fillMaxSize()
            .setIf(isNetworkAvailable) { pullRefresh(pullRefreshState) }
    ) {
        CategoryScreen(
            viewState = viewState,
            onCategoryClick = {
                onNavigate.invoke(Screens.Categories.createRoute(it.text, it.url))
            },
            onAudioClick = {
                if (isNetworkAvailable) {
                    val route = Screens.Player(Tabs.Browse.route).createRoute(
                        rawUrl = it.url,
                        stationName = it.text
                    )
                    onNavigate.invoke(route)
                }
            },
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
    defTitle: String
) {
    val topBar = LocalTopbar.current
    val displayBackButton by rememberSaveable(categoryTitle) { mutableStateOf(categoryTitle != defTitle) }
    LaunchedEffect(Unit, displayBackButton) {
        topBar.invoke(TopBarState(title = categoryTitle, displayBackButton = displayBackButton))
    }
}

@Composable
internal fun CategoryScreen(
    viewState: ViewState,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onAction: (ViewAction) -> Unit
) {
    when (viewState) {
        is ViewState.NothingAvailable -> ErrorView()
        is ViewState.Loading -> ShimmerLoading(Modifier.defListItem())
        is ViewState.CategoriesLoaded -> {

            val categoryItems by viewState.categoryItems.collectAsStateWithLifecycle(emptyList())
            val filterHeaderItems by viewState.filterHeaderItems.collectAsStateWithLifecycle()
            val itemsWithLocation by viewState.itemsWithLocation.collectAsStateWithLifecycle(null)

            MainContent(
                categoryItems = categoryItems,
                filterHeaderItems = filterHeaderItems,
                itemsWithLocation = itemsWithLocation,
                onHeaderFilterClick = { onAction.invoke(ViewAction.FilterByHeader(it)) },
                onCategoryClick = onCategoryClick,
                onAudioClick = onAudioClick,
                onFavClick = { onAction.invoke(ViewAction.ToggleFavorite(it)) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    categoryItems: List<HeaderWithItems>,
    filterHeaderItems: List<CategoryItemDto>?,
    itemsWithLocation: Pair<LatLngBounds, List<CategoryItemDto>>?,
    onHeaderFilterClick: (CategoryItemDto) -> Unit,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    ComposedTimberD("CategoriesScreen - MainContent")

    val config = LocalConfiguration.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var needShowMap by rememberSaveable { mutableStateOf(false) }

    val columnCount = remember(config) {
        var count = 1
        if (config.isTablet()) count += 1
        if (config.isLandscape()) count += 1
        return@remember count
    }

    LaunchedEffect(categoryItems.size) {
        listState.scrollToItem(0)
    }

    Box {
        Column {
            if (!filterHeaderItems.isNullOrEmpty()) {
                FiltersHeader(filterHeaderItems, onHeaderFilterClick)
            }

            // needs animation or some transition
            if (needShowMap && itemsWithLocation != null) {
                ShowMap(itemsWithLocation, onAudioClick)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(LAZY_LIST),
                    state = listState,
                    userScrollEnabled = listState.canScrollForward || listState.canScrollBackward,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 50.dp, top = 4.dp)
                ) {
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
            }
        }

        val bottomPadding by animateDpAsState(targetValue = if (LocalPlayerVisibility.current) 64.dp else 16.dp, label = String.EMPTY)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = bottomPadding,
                    end = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                modifier = Modifier.padding(bottom = 8.dp),
                visible = listState.canScrollBackward,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                SmallFloatingActionButton(
                    modifier = Modifier.testTag(SCROLL_TOP_FAB),
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    content = { Icon(Icons.Rounded.ArrowUpward, null) }
                )
            }

            AnimatedVisibility(
                visible = itemsWithLocation != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.testTag(CategoryScreenTestTags.MAP_FAB),
                    onClick = { needShowMap = !needShowMap },
                    containerColor = FloatingActionButtonDefaults.containerColor,
                    content = { Icon(Icons.Outlined.Map, null) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FiltersHeader(
    headerItems: List<CategoryItemDto>,
    onHeaderFilterClick: (CategoryItemDto) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CategoryScreenTestTags.FILTER_HEADER),
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
                .fillMaxWidth()
                .testTag(CategoryScreenTestTags.HEADER),
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
            modifier = Modifier
                .defListItem()
                .animateItemPlacement(),
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
                .heightIn(max = columnHeight)
                .animateItemPlacement(),
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
                    modifier = Modifier
                        .defListItem()
                        .animateItemPlacement(),
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
private fun DrawItems(
    modifier: Modifier,
    itemDto: CategoryItemDto,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    val context = LocalContext.current
    val snackbar = LocalSnackbar.current
    val coroutineScope = rememberCoroutineScope()

    when (itemDto.type) {
        DtoItemType.CATEGORY -> CategoryListItem(
            modifier = modifier.testTag(CategoryScreenTestTags.CATEGORY),
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

        DtoItemType.SUBCATEGORY -> SubCategoryListItem(
            modifier = modifier.testTag(CategoryScreenTestTags.SUBCATEGORY),
            itemDto = itemDto,
            onCategoryClick = onCategoryClick
        )

        DtoItemType.HEADER -> {}
    }
}