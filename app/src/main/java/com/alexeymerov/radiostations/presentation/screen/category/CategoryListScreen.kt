package com.alexeymerov.radiostations.presentation.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import timber.log.Timber


@Composable
fun CategoryListScreen(
    categoryUrl: String,
    viewModel: CategoryListViewModel = hiltViewModel(),
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    CallOnLaunch { viewModel.setAction(CategoryListViewModel.ViewAction.LoadCategories(categoryUrl)) }
    CallOnDispose { viewModel.clear() }
    CreateMainContent(viewModel, onCategoryClick, onAudioClick)
}

@Composable
private fun CreateMainContent(
    viewModel: CategoryListViewModel,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${viewState.javaClass.simpleName}")
    when (val state = viewState) {
        CategoryListViewModel.ViewState.NothingAvailable -> ErrorView()
        CategoryListViewModel.ViewState.Loading -> LoaderView()
        is CategoryListViewModel.ViewState.CategoriesLoaded -> MainContent(state.list, onCategoryClick, onAudioClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    categoryItems: List<CategoryItemDto>,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(
            items = categoryItems,
            key = { _, item -> item.url.ifEmpty { item.text } },
            contentType = { _, item -> item.type }
        ) { index, itemDto ->
            val defaultModifier = Modifier
                .fillMaxWidth()
                .animateItemPlacement()

            when (itemDto.type) {
                DtoItemType.CATEGORY -> CategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.HEADER -> HeaderListItem(defaultModifier, itemDto)
                DtoItemType.SUBCATEGORY -> SubCategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.AUDIO -> StationListItem(defaultModifier, itemDto, onAudioClick)
            }

            if (index != categoryItems.size - 1
                && itemDto.type == DtoItemType.SUBCATEGORY
                && categoryItems.getOrNull(index + 1)?.type != DtoItemType.HEADER
            ) {
                Divider(
                    Modifier.padding(horizontal = 32.dp),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun HeaderListItem(modifier: Modifier, itemDto: CategoryItemDto) {
    Row(
        modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemDto.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = { onCategoryClick.invoke(itemDto) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = itemDto.text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SubCategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    Row(
        modifier = modifier
            .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { onCategoryClick.invoke(itemDto) }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemDto.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
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
            modifier = modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                    onClick = { onAudioClick.invoke(itemDto) }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val vectorPainter = painterResource(id = R.drawable.full_image)
            Box {
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(itemDto.image)
                        .crossfade(500)
                        .build(),
                    contentDescription = null,
                    error = vectorPainter
                )
            }

            Text(
                text = itemDto.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
