package com.alexeymerov.radiostations.presentation.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.common.CallOnDispose
import com.alexeymerov.radiostations.common.CallOnLaunch
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import com.alexeymerov.radiostations.presentation.common.BasicText
import com.alexeymerov.radiostations.presentation.common.ErrorView
import com.alexeymerov.radiostations.presentation.common.ShimmerLoading
import com.alexeymerov.radiostations.presentation.common.StationListItem
import com.alexeymerov.radiostations.presentation.navigation.Screens
import com.alexeymerov.radiostations.presentation.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.screen.category.CategoriesViewModel.*
import timber.log.Timber


@Composable
fun BaseCategoryScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    defTitle: String,
    categoryTitle: String,
    parentRoute: String,
    onNavigate: (String) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    if (isVisibleToUser) TopBarSetup(categoryTitle, defTitle, topBarBlock)

    CallOnDispose { viewModel.clear() }
    CallOnLaunch { viewModel.setAction(ViewAction.LoadCategories) }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.categoriesFlow.collectAsStateWithLifecycle()

    CategoryScreen(
        viewState = viewState,
        categoryItems = categoryItems,
        parentRoute = parentRoute,
        onNavigate = onNavigate,
        onAction = { viewModel.setAction(it) }
    )
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
    categoryItems: List<CategoryItemDto>,
    parentRoute: String,
    onNavigate: (String) -> Unit,
    onAction: (ViewAction) -> Unit
) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    categoryItems: List<CategoryItemDto>,
    headerItems: List<CategoryItemDto>,
    onHeaderFilterClick: (CategoryItemDto) -> Unit,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (headerItems.isNotEmpty()) {
            item { AddFiltersHeaders(headerItems, onHeaderFilterClick) }
        }
        items(
            items = categoryItems,
            key = CategoryItemDto::id,
            contentType = CategoryItemDto::type
        ) { itemDto ->
            val defaultModifier = Modifier
                .fillMaxWidth()
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
                DtoItemType.HEADER -> HeaderListItem(defaultModifier, itemDto)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun AddFiltersHeaders(headerItems: List<CategoryItemDto>, onHeaderFilterClick: (CategoryItemDto) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
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

@Composable
fun HeaderListItem(modifier: Modifier, itemDto: CategoryItemDto) {
    Row(modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        BasicText(
            text = itemDto.text,
            textStyle = MaterialTheme.typography.titleSmall
        )

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary),
        ) {
            BasicText(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = itemDto.subItemsCount.toString(),
                textStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onPrimary)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    Card(
        modifier = modifier
            .height(60.dp)
            .padding(horizontal = 16.dp),
        onClick = { onCategoryClick.invoke(itemDto) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = itemDto.text
            )
        }
    }
}

@Composable
fun SubCategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { onCategoryClick.invoke(itemDto) }
            )
            .padding(
                start = 32.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 4.dp
            ), // we need to duplicate paddings on each item for unbounded selection here
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(text = itemDto.text, textStyle = MaterialTheme.typography.titleSmall)

        Icon(
            imageVector = Icons.Rounded.KeyboardArrowRight,
            contentDescription = String.EMPTY
        )
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
    MainContent(categoryItems, headers, {}, {}, {}, {})
}