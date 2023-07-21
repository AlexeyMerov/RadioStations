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
import androidx.compose.material3.Divider
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
                DtoItemType.HEADER -> HeaderListItem(defaultModifier, itemDto)
                DtoItemType.CATEGORY -> CategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.SUBCATEGORY -> SubCategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.AUDIO -> StationListItem(defaultModifier, itemDto, onAudioClick)
            }

            if (index != categoryItems.size - 1) {
                val padding = if (itemDto.type == DtoItemType.HEADER || itemDto.type == DtoItemType.SUBCATEGORY) 16.dp else 8.dp
                Divider(
                    Modifier.padding(horizontal = padding),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun HeaderListItem(modifier: Modifier, itemDto: CategoryItemDto) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemDto.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp)
        )
    }
}

@Composable
fun CategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    Row(
        modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
            onClick = { onCategoryClick.invoke(itemDto) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemDto.text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SubCategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    Row(modifier.clickable(
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
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = String.EMPTY,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun StationListItem(modifier: Modifier, itemDto: CategoryItemDto, onAudioClick: (CategoryItemDto) -> Unit) {
    Row(modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
        onClick = { onAudioClick.invoke(itemDto) }
    ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val vectorPainter = painterResource(id = R.drawable.full_image)
        Box(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
            AsyncImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(itemDto.image)
                    .crossfade(200)
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
            modifier = Modifier.padding(start = 12.dp, top = 24.dp, bottom = 24.dp, end = 16.dp)
        )
    }
}
