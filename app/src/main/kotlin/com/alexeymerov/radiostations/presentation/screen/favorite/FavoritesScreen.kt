package com.alexeymerov.radiostations.presentation.screen.favorite

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.CallOnDispose
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.presentation.screen.common.ErrorView
import com.alexeymerov.radiostations.presentation.screen.common.LoaderView
import com.alexeymerov.radiostations.presentation.screen.common.StationListItem
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoritesViewModel.*
import timber.log.Timber


@Composable
fun FavoriteListScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onAudioClick: (CategoryItemDto) -> Unit
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    CallOnDispose { viewModel.clear() }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.categoriesFlow.collectAsStateWithLifecycle()

    when (viewState) {
        is ViewState.NothingAvailable -> ErrorView(stringResource(R.string.you_need_to_add_some_stations), showImage = false)
        is ViewState.Loading -> LoaderView()
        is ViewState.FavoritesLoaded -> {
            MainContent(
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
    categoryItems: List<CategoryItemDto>,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit
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

            StationListItem(defaultModifier, itemDto, onAudioClick, onFavClick)
        }
    }
}