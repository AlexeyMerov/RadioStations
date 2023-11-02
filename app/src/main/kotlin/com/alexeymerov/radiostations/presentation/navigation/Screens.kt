package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.screen.category.CategoryListScreen
import com.alexeymerov.radiostations.presentation.screen.favorite.BaseFavoriteScreen
import com.alexeymerov.radiostations.presentation.screen.player.PlayerScreen
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileScreen
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsScreen
import timber.log.Timber


fun NavGraphBuilder.categoriesScreen(parentRoute: String, navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Categories.route,
        arguments = createListOfStringArgs(Screens.Categories.Const.ARG_TITLE, Screens.Categories.Const.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.categoriesScreen")

        val defTitle = stringResource(R.string.browse)
        val categoryTitle by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Categories.Const.ARG_TITLE).ifEmpty { defTitle }) }

        CategoryListScreen(
            appBarBlock = appBarBlock,
            defTitle = defTitle,
            categoryTitle = categoryTitle,
            parentRoute = parentRoute,
            onNavigate = { navController.navigate(it) }
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
        val locationName by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_SUBTITLE)) }
        val stationImgUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_IMG_URL)) }
        val rawUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_URL)) }

        PlayerScreen(
            appBarBlock = appBarBlock,
            stationName = stationName,
            locationName = locationName,
            stationImgUrl = stationImgUrl.decodeUrl(),
            rawUrl = rawUrl.decodeUrl()
        )
    }
}

fun NavGraphBuilder.favoritesScreen(parentRoute: String, navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Favorites.route,
        arguments = createListOfStringArgs(Screens.Favorites.Const.ARG_TITLE),
    ) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.favoritesScreen")

        BaseFavoriteScreen(
            appBarBlock = appBarBlock,
            parentRoute = parentRoute,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.profileScreen(navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Profile.route,
        arguments = createListOfStringArgs(Screens.Profile.Const.ARG_TITLE),
    ) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.profileScreen")

        ProfileScreen(
            appBarBlock = appBarBlock,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.settingsScreen(navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Settings.route,
        arguments = createListOfStringArgs(Screens.Settings.Const.ARG_TITLE),
    ) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.settingsScreen")

        SettingsScreen(appBarBlock = appBarBlock)
    }
}