package com.alexeymerov.radiostations.presentation.screen.favorite

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.CallOnDispose
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCase.*
import com.alexeymerov.radiostations.presentation.navigation.Screens
import com.alexeymerov.radiostations.presentation.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.screen.common.DropDownItem
import com.alexeymerov.radiostations.presentation.screen.common.ErrorView
import com.alexeymerov.radiostations.presentation.screen.common.LoaderView
import com.alexeymerov.radiostations.presentation.screen.common.StationListItem
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoritesViewModel.*
import timber.log.Timber


@Composable
fun BaseFavoriteScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    parentRoute: String,
    onNavigate: (String) -> Unit,
) {
    val selectedItemsCount = viewModel.selectedItemsCount

    if (isVisibleToUser) {
        TopBarSetup(
            selectedItemsCount = selectedItemsCount,
            topBarBlock = topBarBlock,
            onAction = { viewModel.setAction(it) }
        )
    }

    CallOnDispose { viewModel.clear() }
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.categoriesFlow.collectAsStateWithLifecycle()

    FavoriteScreen(
        viewState = viewState,
        categoryItems = categoryItems,
        onAudioClick = {
            val route = Screens.Player(parentRoute).createRoute(it.text, it.subText.orEmpty(), it.image.orEmpty(), it.url)
            onNavigate.invoke(route)
        },
        inSelection = selectedItemsCount > 0,
        onFavClick = { viewModel.setAction(ViewAction.ToggleFavorite(it)) },
        onLongClick = { viewModel.setAction(ViewAction.SelectItem(it)) }
    )
}

@Composable
private fun TopBarSetup(
    selectedItemsCount: Int,
    topBarBlock: (TopBarState) -> Unit,
    onAction: (ViewAction) -> Unit
) {
    val title = stringResource(R.string.favorites)
    val rightIcon = ImageVector.vectorResource(R.drawable.icon_settings)

    LaunchedEffect(Unit, selectedItemsCount) {
        val topBarState = when (selectedItemsCount) {
            0 -> {
                TopBarState(
                    title = title,
                    rightIcon = rightIcon,
                    dropDownMenu = {
                        DropDownItems { type ->
                            onAction.invoke(ViewAction.SetViewType(type))
                        }
                    }
                )
            }

            else -> {
                TopBarState(
                    title = "Selected: $selectedItemsCount",
                    selectedItems = selectedItemsCount,
                    rightIcon = Icons.Rounded.StarHalf,
                    rightIconAction = { onAction.invoke(ViewAction.UnfavoriteSelected) }
                )
            }
        }

        topBarBlock.invoke(topBarState)
    }
}

@Composable
private fun DropDownItems(onClick: (ViewType) -> Unit) {
    // not hiding after selection is intentional
    DropDownItem(
        iconId = R.drawable.icon_rows,
        text = stringResource(R.string.rows),
        action = { onClick.invoke(ViewType.LIST) }
    )
    DropDownItem(
        iconId = R.drawable.icon_grid_2,
        text = stringResource(R.string.grid_2),
        action = { onClick.invoke(ViewType.GRID_2_COLUMN) }
    )
    DropDownItem(
        iconId = R.drawable.icon_grid_3,
        text = stringResource(R.string.grid_3),
        action = { onClick.invoke(ViewType.GRID_3_COLUMN) }
    )
}

@Composable
fun FavoriteScreen(
    viewState: ViewState,
    categoryItems: List<CategoryItemDto>,
    inSelection: Boolean,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit,
    onLongClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    when (viewState) {
        is ViewState.NothingAvailable -> ErrorView(stringResource(R.string.you_need_to_add_some_stations), showImage = false)
        is ViewState.Loading -> LoaderView()
        is ViewState.FavoritesLoaded -> {
            MainContent(
                viewType = viewState.viewType,
                categoryItems = categoryItems,
                inSelection = inSelection,
                onAudioClick = onAudioClick,
                onFavClick = onFavClick,
                parentLongClick = onLongClick
            )
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
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    val isList by rememberSaveable(viewType) { mutableStateOf(viewType == ViewType.LIST) }
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(viewType.value),
        contentPadding = PaddingValues(horizontal = if (isList) 0.dp else 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = categoryItems,
            key = CategoryItemDto::url,
            contentType = CategoryItemDto::type
        ) { itemDto ->
            val defaultModifier = Modifier.animateItemPlacement()

            var isSelected by rememberSaveable { mutableStateOf(false) }

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
                    modifier = defaultModifier,
                    itemDto = itemDto,
                    inSelection = inSelection,
                    isSelected = isSelected,
                    onAudioClick = onAudioOrSelection,
                    onFavClick = onFavClick,
                    onLongClick = onLongClick
                )
            } else {
                StationGridItem(
                    modifier = defaultModifier,
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