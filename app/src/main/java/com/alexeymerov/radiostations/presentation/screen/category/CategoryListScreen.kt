package com.alexeymerov.radiostations.presentation.screen.category

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import com.alexeymerov.radiostations.presentation.Screens
import com.alexeymerov.radiostations.presentation.common.ErrorView
import com.alexeymerov.radiostations.presentation.common.LoaderView
import timber.log.Timber


@Composable
fun CategoryListScreen(
    navController: NavHostController,
    categoryUrl: String,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    CreateMainContent(viewModel, categoryUrl, navController)
}

@Composable
private fun CreateMainContent(
    viewModel: CategoryListViewModel,
    categoryUrl: String,
    navController: NavHostController
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val categoryItems by viewModel.getCategories(categoryUrl).collectAsStateWithLifecycle(initialValue = emptyList())
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] new state: $viewState")
    when (viewState) {
        CategoryListViewModel.ViewState.NothingAvailable -> ErrorView()
        CategoryListViewModel.ViewState.Loading -> LoaderView()
        CategoryListViewModel.ViewState.CategoriesLoaded -> MainContent(categoryItems, navController)
    }
    LaunchedEffect(Unit) { viewModel.setAction(CategoryListViewModel.ViewAction.LoadCategories(categoryUrl)) }
}

@Composable
private fun MainContent(
    categoryItems: List<CategoryItemDto>,
    navController: NavHostController
) {
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(categoryItems) { index, itemDto ->
            when (itemDto.type) {
                DtoItemType.HEADER -> HeaderListItem(itemDto)
                DtoItemType.CATEGORY -> CategoryListItem(navController, itemDto)
                DtoItemType.SUBCATEGORY -> SubCategoryListItem(navController, itemDto)
                DtoItemType.AUDIO -> StationListItem(navController, itemDto)
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
fun HeaderListItem(itemDto: CategoryItemDto) {
    Row(
        Modifier.fillMaxWidth(),
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
fun CategoryListItem(navController: NavHostController, itemDto: CategoryItemDto) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { navController.navigate(Screens.Categories.createRoute(itemDto.text, itemDto.url)) }
            ),
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
fun SubCategoryListItem(navController: NavHostController, itemDto: CategoryItemDto) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { navController.navigate(Screens.Categories.createRoute(itemDto.text, itemDto.url)) }
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
fun StationListItem(navController: NavHostController, itemDto: CategoryItemDto) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { navController.navigate(Screens.Player.createRoute(itemDto.text, itemDto.image.orEmpty(), itemDto.url)) }
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
