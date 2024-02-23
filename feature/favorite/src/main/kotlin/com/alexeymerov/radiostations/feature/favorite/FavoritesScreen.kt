package com.alexeymerov.radiostations.feature.favorite

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.ViewType
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.LocalConnectionStatus
import com.alexeymerov.radiostations.core.ui.common.LocalSnackbar
import com.alexeymerov.radiostations.core.ui.extensions.defListItemModifier
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isTablet
import com.alexeymerov.radiostations.core.ui.navigation.DropDownItem
import com.alexeymerov.radiostations.core.ui.navigation.RightIconItem
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.TopBarIcon
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.core.ui.view.ErrorView
import com.alexeymerov.radiostations.core.ui.view.LoaderView
import com.alexeymerov.radiostations.core.ui.view.StationListItem
import com.alexeymerov.radiostations.feature.favorite.FavoriteTestTags.GRID_ITEM
import com.alexeymerov.radiostations.feature.favorite.FavoriteTestTags.LAZY_LIST_GRID
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.ViewAction
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.ViewState
import kotlinx.coroutines.launch


@Composable
fun BaseFavoriteScreen(
    viewModel: FavoritesViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    parentRoute: String,
    onNavigate: (String) -> Unit,
) {
    val isNetworkAvailable = LocalConnectionStatus.current
    val selectedItemsCount by viewModel.selectedItemsCount

    if (isVisibleToUser) {
        TopBarSetup(
            selectedItemsCount = selectedItemsCount,
            topBarBlock = topBarBlock,
            onAction = { viewModel.setAction(it) }
        )
    }

    val inSelection by remember(selectedItemsCount) {
        derivedStateOf {
            selectedItemsCount > 0
        }
    }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val viewEffect by viewModel.viewEffect.collectAsStateWithLifecycle(initialValue = null)

    FavoriteScreen(
        viewState = viewState,
        viewEffect = viewEffect,
        inSelection = inSelection,
        onAudioClick = {
            if (isNetworkAvailable) {
                val route = Screens.Player(parentRoute).createRoute(
                    stationName = it.text,
                    subTitle = it.subTitle.orEmpty(),
                    stationImgUrl = it.image.orEmpty(),
                    rawUrl = it.url,
                    id = it.id,
                    isFav = it.isFavorite
                )
                onNavigate.invoke(route)
            }
        },
        onAction = { viewModel.setAction(it) }
    )
}

@Composable
private fun TopBarSetup(
    selectedItemsCount: Int,
    topBarBlock: (TopBarState) -> Unit,
    onAction: (ViewAction) -> Unit
) {
    val title = stringResource(R.string.favorites)
    LaunchedEffect(Unit, selectedItemsCount) {
        val topBarState = when (selectedItemsCount) {
            0 -> {
                TopBarState(
                    title = title,
                    rightIcon = RightIconItem(TopBarIcon.SETTINGS).apply {
                        dropDownMenu = dropDownItems { viewType ->
                            onAction.invoke(ViewAction.SetViewType(viewType))
                        }
                    }
                )
            }

            else -> {
                TopBarState(
                    title = "Selected: $selectedItemsCount",
                    selectedItems = selectedItemsCount,
                    rightIcon = RightIconItem(TopBarIcon.STAR_HALF).apply {
                        action = { onAction.invoke(ViewAction.UnfavoriteSelected) }
                    }
                )
            }
        }

        topBarBlock.invoke(topBarState)
    }
}

private fun dropDownItems(onClick: (ViewType) -> Unit): List<DropDownItem> = listOf(
    // not hiding after selection is intentional
    DropDownItem(
        iconId = R.drawable.icon_rows,
        stringId = R.string.rows,
    ).apply { action = { onClick.invoke(ViewType.LIST) } },

    DropDownItem(
        iconId = R.drawable.icon_grid_2,
        stringId = R.string.grid_2,
    ).apply { action = { onClick.invoke(ViewType.GRID_2_COLUMN) } },

    DropDownItem(
        iconId = R.drawable.icon_grid_3,
        stringId = R.string.grid_3,
    ).apply { action = { onClick.invoke(ViewType.GRID_3_COLUMN) } }
)

@Composable
internal fun FavoriteScreen(
    viewState: ViewState,
    viewEffect: FavoritesViewModel.ViewEffect?,
    inSelection: Boolean,
    onAction: (ViewAction) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    val snackbar = LocalSnackbar.current
    LaunchedEffect(inSelection) {
        if (inSelection) {
            snackbar.currentSnackbarData?.dismiss()
        }
    }

    HandleEffects(viewEffect, onAction)

    when (viewState) {
        is ViewState.Loading -> LoaderView()

        is ViewState.NothingAvailable -> {
            ErrorView(
                errorText = stringResource(R.string.you_need_to_add_some_stations),
                showImage = false
            )
        }

        is ViewState.FavoritesLoaded -> {
            MainContent(
                viewType = viewState.viewType,
                categoryItems = viewState.items,
                inSelection = inSelection,
                onAudioClick = onAudioClick,
                onFavClick = { onAction.invoke(ViewAction.Unfavorite(it)) },
                parentLongClick = { onAction.invoke(ViewAction.SelectItem(it)) }
            )
        }
    }
}

@Composable
private fun HandleEffects(
    viewEffect: FavoritesViewModel.ViewEffect?,
    onAction: (ViewAction) -> Unit
) {
    val snackbar = LocalSnackbar.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    DisposableEffect(viewEffect) {
        if (viewEffect is FavoritesViewModel.ViewEffect.ShowUnfavoriteToast) {
            coroutineScope.launch {
                snackbar.currentSnackbarData?.dismiss()
                val result = snackbar.showSnackbar(
                    message = "${context.getString(R.string.removed)}: ${viewEffect.itemCount}",
                    actionLabel = context.getString(R.string.undo),
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onAction.invoke(ViewAction.UndoRecentUnfavorite)
                }
            }
        }

        onDispose {
            snackbar.currentSnackbarData?.dismiss()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    viewType: ViewType,
    categoryItems: List<CategoryItemDto>,
    inSelection: Boolean,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit,
    parentLongClick: (CategoryItemDto) -> Unit
) {
    ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    val config = LocalConfiguration.current
    val isList by rememberSaveable(viewType) { mutableStateOf(viewType == ViewType.LIST) }

    val columnCount = remember(viewType, config) {
        var count = viewType.columnCount
        if (config.isTablet()) count += 1
        if (config.isLandscape()) count += 1
        return@remember count
    }
    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .testTag(LAZY_LIST_GRID),
        state = gridState,
        userScrollEnabled = gridState.canScrollBackward || gridState.canScrollForward,
        columns = GridCells.Fixed(columnCount),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 50.dp, top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = categoryItems,
            key = CategoryItemDto::id,
            contentType = CategoryItemDto::type
        ) { itemDto ->
            var isSelected by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(inSelection) {
                if (!inSelection) isSelected = false
            }

            val onLongClick: (CategoryItemDto) -> Unit = {
                isSelected = !isSelected
                parentLongClick.invoke(it)
            }

            val onAudioOrSelection: (CategoryItemDto) -> Unit = {
                if (inSelection) {
                    onLongClick.invoke(it)
                } else {
                    onAudioClick.invoke(it)
                }
            }

            if (isList) { // try animate between rows and grid
                StationListItem(
                    modifier = defListItemModifier.animateItemPlacement(),
                    itemDto = itemDto,
                    inSelection = inSelection,
                    isSelected = isSelected,
                    onAudioClick = onAudioOrSelection,
                    onFavClick = onFavClick,
                    onLongClick = onLongClick
                )
            } else {
                StationGridItem(
                    modifier = Modifier
                        .animateItemPlacement()
                        .testTag(GRID_ITEM),
                    itemDto = itemDto,
                    inSelection = inSelection,
                    isSelected = isSelected,
                    onAudioClick = onAudioOrSelection,
                    onFavClick = onFavClick,
                    onLongClick = onLongClick
                )
            }
        }
    }
}