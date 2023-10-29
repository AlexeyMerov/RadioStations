package com.alexeymerov.radiostations.presentation.screen.favorite

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.CallOnDispose
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCase.*
import com.alexeymerov.radiostations.presentation.screen.common.BasicText
import com.alexeymerov.radiostations.presentation.screen.common.ErrorView
import com.alexeymerov.radiostations.presentation.screen.common.LoaderView
import com.alexeymerov.radiostations.presentation.screen.common.StationListItem
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoritesViewModel.*
import timber.log.Timber


@Composable
fun FavoriteListScreen(
    viewModel: FavoritesViewModel,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    CallOnDispose { viewModel.clear() }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.categoriesFlow.collectAsStateWithLifecycle()

    when (val state = viewState) {
        is ViewState.NothingAvailable -> ErrorView(stringResource(R.string.you_need_to_add_some_stations), showImage = false)
        is ViewState.Loading -> LoaderView()
        is ViewState.FavoritesLoaded -> {
            MainContent(
                viewType = state.viewType,
                categoryItems = categoryItems,
                onAudioClick = onAudioClick,
                onFavClick = { viewModel.setAction(ViewAction.ToggleFavorite(it)) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    viewType: ViewType,
    categoryItems: List<CategoryItemDto>,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    val isList by rememberSaveable(viewType) { mutableStateOf(viewType == ViewType.LIST) }

    if (isList) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = categoryItems,
                key = CategoryItemDto::url,
                contentType = CategoryItemDto::type
            ) { itemDto ->
                val defaultModifier = Modifier.animateItemPlacement()
                StationListItem(defaultModifier, itemDto, onAudioClick, onFavClick)
            }
        }
    } else {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(viewType.value),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = categoryItems,
                key = CategoryItemDto::url,
                contentType = CategoryItemDto::type
            ) { itemDto ->
                val defaultModifier = Modifier.animateItemPlacement()
                StationGridItem(defaultModifier, itemDto, onAudioClick, onFavClick)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StationGridItem(modifier: Modifier, itemDto: CategoryItemDto, onAudioClick: (CategoryItemDto) -> Unit, onFavClick: (CategoryItemDto) -> Unit) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                    onClick = { onAudioClick.invoke(itemDto) }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                val placeholderPainter = painterResource(id = R.drawable.full_image)
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
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

                AnimatedContent(
                    modifier = Modifier.align(Alignment.TopEnd),
                    targetState = itemDto.isFavorite,
                    label = "Star",
                    transitionSpec = { scaleIn().togetherWith(scaleOut()) }
                ) {
                    IconButton(onClick = { onFavClick.invoke(itemDto) }) {
                        Icon(
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            ),
                            imageVector = if (it) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = String.EMPTY,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            BasicText(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .basicMarquee(),
                text = itemDto.text
            )

            itemDto.subText?.let { subtext ->
                Row(
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .alpha(0.7f)
                            .size(12.dp),
                        imageVector = Icons.Outlined.LocationCity,
                        contentDescription = String.EMPTY
                    )

                    BasicText(
                        modifier = Modifier
                            .alpha(0.7f)
                            .padding(start = 4.dp),
                        text = subtext,
                        textStyle = MaterialTheme.typography.labelMedium
                    )
                }

            }
        }
    }
}