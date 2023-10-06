package com.alexeymerov.radiostations.presentation.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
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

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CreateMainContent(viewState, onCategoryClick, onAudioClick)
}

@Composable
private fun CreateMainContent(
    viewState: CategoryListViewModel.ViewState,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${viewState.javaClass.simpleName}")
    when (viewState) {
        is CategoryListViewModel.ViewState.NothingAvailable -> ErrorView()
        is CategoryListViewModel.ViewState.Loading -> LoaderView()
        is CategoryListViewModel.ViewState.CategoriesLoaded -> MainContent(viewState.list, onCategoryClick, onAudioClick)
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
                DtoItemType.HEADER -> HeaderListItem(defaultModifier, itemDto)
                DtoItemType.SUBCATEGORY -> SubCategoryListItem(defaultModifier, itemDto, onCategoryClick)
                DtoItemType.AUDIO -> StationListItem(defaultModifier, itemDto, onAudioClick)
                DtoItemType.DIVIDER -> Divider(Modifier.padding(horizontal = 32.dp), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun HeaderListItem(modifier: Modifier, itemDto: CategoryItemDto) {
    Row(modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)) {
        BasicText(text = itemDto.text, textStyle = MaterialTheme.typography.titleSmall)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(text = itemDto.text)
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
            .padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
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
            val vectorPainter = painterResource(id = R.drawable.full_image)
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
                error = vectorPainter,
                placeholder = vectorPainter
            )

            BasicText(text = itemDto.text, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun BasicText(modifier: Modifier = Modifier, text: String, textStyle: TextStyle = MaterialTheme.typography.titleMedium) {
    Text(
        text = text,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}