package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.screen.category.BaseCategoryScreen
import com.alexeymerov.radiostations.presentation.screen.favorite.BaseFavoriteScreen
import com.alexeymerov.radiostations.presentation.screen.player.BasePlayerScreen
import com.alexeymerov.radiostations.presentation.screen.profile.BaseProfileScreen
import com.alexeymerov.radiostations.presentation.screen.settings.BaseSettingsScreen
import timber.log.Timber


fun NavGraphBuilder.categoriesScreen(parentRoute: String, navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Categories.route,
        arguments = createListOfStringArgs(Screens.Categories.Const.ARG_TITLE, Screens.Categories.Const.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.categoriesScreen")

        val defTitle = stringResource(R.string.browse)
        val categoryTitle by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Categories.Const.ARG_TITLE).ifEmpty { defTitle }) }

        BaseCategoryScreen(
            isVisibleToUser = navController.isVisibleToUser(Screens.Categories.Const.ROUTE),
            topBarBlock = topBarBlock,
            defTitle = defTitle,
            categoryTitle = categoryTitle,
            parentRoute = parentRoute,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.playerScreen(parentRoute: String, navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Player(parentRoute).route,
        arguments = createListOfStringArgs(Screens.Player.Const.ARG_TITLE, Screens.Player.Const.ARG_IMG_URL, Screens.Player.Const.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

        val stationName by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_TITLE)) }
        val locationName by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_SUBTITLE)) }
        val stationImgUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_IMG_URL)) }
        val rawUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(Screens.Player.Const.ARG_URL)) }

        BasePlayerScreen(
            isVisibleToUser = navController.isVisibleToUser(Screens.Player.Const.ROUTE),
            topBarBlock = topBarBlock,
            stationName = stationName,
            locationName = locationName,
            stationImgUrl = stationImgUrl.decodeUrl(),
            rawUrl = rawUrl.decodeUrl()
        )
    }
}

fun NavGraphBuilder.favoritesScreen(parentRoute: String, navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Favorites.route,
        arguments = createListOfStringArgs(Screens.Favorites.Const.ARG_TITLE),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.favoritesScreen")

        BaseFavoriteScreen(
            isVisibleToUser = navController.isVisibleToUser(Screens.Favorites.Const.ROUTE),
            topBarBlock = topBarBlock,
            parentRoute = parentRoute,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.profileScreen(navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Profile.route,
        arguments = createListOfStringArgs(Screens.Profile.Const.ARG_TITLE),
    ) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.profileScreen")

        BaseProfileScreen(
            isVisibleToUser = navController.isVisibleToUser(Screens.Profile.Const.ROUTE),
            topBarBlock = topBarBlock,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.settingsScreen(navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Settings.route,
        arguments = createListOfStringArgs(Screens.Settings.Const.ARG_TITLE),
    ) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.settingsScreen")

        BaseSettingsScreen(
            isVisibleToUser = navController.isVisibleToUser(Screens.Settings.Const.ROUTE),
            topBarBlock = topBarBlock
        )
    }
}


/**
 * idk...
 * IF you will switch BottomNav items really fast it will cause the wrong TopBar state
 * This approach resolve the issue but looks not right either.
 * Will research later.
 * */
private fun NavHostController.isVisibleToUser(route: String) = currentDestination?.route?.contains(route) ?: true