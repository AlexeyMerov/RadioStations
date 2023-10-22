package com.alexeymerov.radiostations.presentation.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import com.alexeymerov.radiostations.presentation.common.CallOnDispose
import com.alexeymerov.radiostations.presentation.common.CallOnLaunch
import com.alexeymerov.radiostations.presentation.common.ErrorView
import com.alexeymerov.radiostations.presentation.common.LoaderView
import com.alexeymerov.radiostations.presentation.screen.category.CategoryListViewModel.*
import timber.log.Timber


@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel = hiltViewModel(),
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    CallOnLaunch { viewModel.setAction(ViewAction.LoadCategories) }
    CallOnDispose { viewModel.clear() }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.categoriesFlow.collectAsStateWithLifecycle()

    when (val state = viewState) {
        is ViewState.NothingAvailable -> ErrorView()
        is ViewState.Loading -> LoaderView()
        is ViewState.CategoriesLoaded -> {
            MainContent(
                categoryItems = categoryItems,
                headerItems = state.headerItems,
                onHeaderFilterClick = { viewModel.setAction(ViewAction.FilterByHeader(it)) },
                onCategoryClick = onCategoryClick,
                onAudioClick = onAudioClick
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
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    LazyColumn(Modifier.fillMaxSize()) {
        if (headerItems.isNotEmpty()) {
            item { AddHeaders(headerItems, onHeaderFilterClick) }
        }
        items(
            items = categoryItems,
            key = CategoryItemDto::url,
            contentType = CategoryItemDto::type
        ) { itemDto ->
            val defaultModifier = Modifier
                .fillMaxWidth()
                .animateItemPlacement()

            when (itemDto.type) {
                DtoItemType.CATEGORY -> CategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.AUDIO -> StationListItem(defaultModifier, itemDto, onAudioClick)
                DtoItemType.SUBCATEGORY -> SubCategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.HEADER -> HeaderListItem(defaultModifier, itemDto)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun AddHeaders(headerItems: List<CategoryItemDto>, onHeaderFilterClick: (CategoryItemDto) -> Unit) {
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
    Row(modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)) {
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
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = { onCategoryClick.invoke(itemDto) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = itemDto.text
        )
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
            .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(text = itemDto.text, textStyle = MaterialTheme.typography.titleSmall)
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = String.EMPTY
        )
    }
}

@Composable
fun StationListItem(modifier: Modifier, itemDto: CategoryItemDto, onAudioClick: (CategoryItemDto) -> Unit) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { onAudioClick.invoke(itemDto) }
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val placeholderPainter = painterResource(id = R.drawable.full_image)
            AsyncImage(
                modifier = Modifier
                    .size(65.dp)
                    .background(MaterialTheme.colorScheme.background),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(itemDto.image)
                    .crossfade(500)
                    .build(),
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
                error = placeholderPainter,
                placeholder = placeholderPainter
            )

            BasicText(text = itemDto.text, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun BasicText(modifier: Modifier = Modifier, text: String, textStyle: TextStyle = MaterialTheme.typography.titleMedium) {
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
private fun MainContentPreview() {
    val categoryItems: List<CategoryItemDto> = listOf(
        CategoryItemDto("url#1", "Header", type = DtoItemType.HEADER, subItemsCount = 1),
        CategoryItemDto("url#2", "Category", type = DtoItemType.CATEGORY),
        CategoryItemDto("url#3", "Very Long Category Name", type = DtoItemType.CATEGORY),
        CategoryItemDto("url#4", "Another Header", type = DtoItemType.HEADER, subItemsCount = 22),
        CategoryItemDto("url#5", "Subcategory", type = DtoItemType.SUBCATEGORY),
        CategoryItemDto("url#6", "Long Subcategory", type = DtoItemType.SUBCATEGORY),
        CategoryItemDto("url#7", "Station (City)", type = DtoItemType.AUDIO),
        CategoryItemDto("url#8", "Station", type = DtoItemType.AUDIO),
    )
    val headers = listOf(
        CategoryItemDto("url#1", "Header", type = DtoItemType.HEADER, isFiltered = true, subItemsCount = 1),
        CategoryItemDto("url#2", "Long Header", type = DtoItemType.HEADER, subItemsCount = 22),
        CategoryItemDto("url#3", "Very Long Header", type = DtoItemType.HEADER, isFiltered = true, subItemsCount = 999),
        CategoryItemDto("url#4", "Tiny", type = DtoItemType.HEADER),
        CategoryItemDto("url#5", "Header", type = DtoItemType.HEADER, isFiltered = true),
    )
    MainContent(categoryItems, headers, {}, {}, {})
}