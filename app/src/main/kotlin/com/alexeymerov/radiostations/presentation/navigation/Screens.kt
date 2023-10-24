package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.screen.category.CategoryListScreen
import com.alexeymerov.radiostations.presentation.screen.favorite.FavoriteListScreen
import com.alexeymerov.radiostations.presentation.screen.player.PlayerScreen
import timber.log.Timber


fun NavGraphBuilder.categoriesScreen(parentRoute: String, navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Categories.route,
        arguments = createListOfStringArgs(Screens.Categories.Const.ARG_TITLE, Screens.Categories.Const.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.categoriesScreen")

        val defTitle = LocalContext.current.getString(R.string.browse)
        val categoryTitle by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Categories.Const.ARG_TITLE).ifEmpty { defTitle }) }
        val displayBackButton by rememberSaveable(categoryTitle) { mutableStateOf(categoryTitle != defTitle) }
        appBarBlock.invoke(AppBarState(title = categoryTitle, displayBackButton = displayBackButton))
        CategoryListScreen(
            onCategoryClick = { navController.navigate(Screens.Categories.createRoute(it.text, it.url)) },
            onAudioClick = { navController.navigate(Screens.Player(parentRoute).createRoute(it.text, it.image.orEmpty(), it.url)) }
        )
    }
}

fun NavGraphBuilder.playerScreen(parentRoute: String, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Player(parentRoute).route,
        arguments = createListOfStringArgs(Screens.Player.Const.ARG_TITLE, Screens.Player.Const.ARG_IMG_URL, Screens.Player.Const.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

        val stationName by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_TITLE)) }
        val stationImgUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_IMG_URL)) }
        val rawUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_URL)) }
        appBarBlock.invoke(AppBarState(title = stationName, displayBackButton = true))
        PlayerScreen(stationImgUrl.decodeUrl(), rawUrl.decodeUrl())
    }
}

fun NavGraphBuilder.favoritesScreen(parentRoute: String, navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Favorites.route,
        arguments = createListOfStringArgs(Screens.Favorites.Const.ARG_TITLE),
    ) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.favoritesScreen")

        appBarBlock.invoke(AppBarState(titleRes = R.string.favorites))
        FavoriteListScreen(
            onAudioClick = { navController.navigate(Screens.Player(parentRoute).createRoute(it.text, it.image.orEmpty(), it.url)) }
        )
    }
}