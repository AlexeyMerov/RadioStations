package com.alexeymerov.radiostations.presentation.screen.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import com.alexeymerov.radiostations.presentation.Screens
import timber.log.Timber


@Composable
fun CategoryListScreen(
    navController: NavHostController,
    displayBackButton: Boolean,
    categoryTitle: String,
    categoryUrl: String,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    Scaffold(
        containerColor = colorResource(R.color.background),
        topBar = { CreateTopBar(categoryTitle, displayBackButton, navController) },
        content = { paddingValues -> CreateMainContent(viewModel, categoryUrl, paddingValues, navController) }
    )

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CreateTopBar(categoryTitle: String, displayBackButton: Boolean, navController: NavHostController) {
    TopAppBar(
        title = { Text(categoryTitle) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.main_200),
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            if (displayBackButton) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    )
}

@Composable
private fun CreateMainContent(
    viewModel: CategoryListViewModel,
    categoryUrl: String,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    when (viewState) {
        CategoryListViewModel.ViewState.NothingAvailable -> {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(R.string.sorry_nothing_available),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        else -> {}
    }

    val data by viewModel.getCategories(categoryUrl).collectAsStateWithLifecycle(initialValue = emptyList())
    LaunchedEffect(Unit) { viewModel.setAction(CategoryListViewModel.ViewAction.LoadCategories(categoryUrl)) }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        itemsIndexed(data) { index, item ->
            when (item.type) {
                DtoItemType.HEADER -> HeaderListItem(item)
                DtoItemType.CATEGORY -> CategoryListItem(item) {
                    navController.navigate(Screens.Categories.createRoute(item.text, item.url))
                }

                DtoItemType.SUBCATEGORY -> SubCategoryListItem(item) {
                    navController.navigate(Screens.Categories.createRoute(item.text, item.url))
                }

                DtoItemType.AUDIO -> StationListItem(item) {
                    navController.navigate(Screens.Player.createRoute(item.text, item.image.orEmpty(), item.url))
                }
            }

            if (index != data.size - 1) {

                if (item.type == DtoItemType.HEADER || item.type == DtoItemType.SUBCATEGORY) {
                    Divider(
                        Modifier.padding(start = 16.dp, end = 16.dp),
                        thickness = 0.5.dp
                    )
                } else {
                    Divider(
                        Modifier.padding(start = 8.dp, end = 8.dp),
                        thickness = 0.5.dp
                    )
                }

            }
        }
    }
}

@Composable
fun HeaderListItem(data: CategoryItemDto) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp)
        )
    }
}

@Composable
fun CategoryListItem(data: CategoryItemDto, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SubCategoryListItem(data: CategoryItemDto, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 32.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = String.EMPTY,
            tint = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun StationListItem(data: CategoryItemDto, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val vectorPainter = painterResource(id = R.drawable.full_image)
        Box(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
            AsyncImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = data.image,
                contentDescription = null,
                placeholder = vectorPainter,
                error = vectorPainter
            )
        }

        Text(
            text = data.text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 12.dp, top = 24.dp, bottom = 24.dp, end = 16.dp)
        )
    }
}
