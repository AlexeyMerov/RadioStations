package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.navigation.decodeUrl
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.feature.category.BaseCategoryScreen
import com.alexeymerov.radiostations.feature.favorite.BaseFavoriteScreen
import com.alexeymerov.radiostations.feature.player.screen.LoadPlayerScreen
import com.alexeymerov.radiostations.feature.player.screen.PreloadedPlayerScreen
import com.alexeymerov.radiostations.feature.profile.BaseProfileScreen
import com.alexeymerov.radiostations.feature.settings.BaseSettingsScreen


fun NavGraphBuilder.categoriesScreen(parentRoute: String, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Categories.route,
        arguments = listOf(
            navArgument(Screens.Categories.Const.ARG_TITLE, defaultStringArg()),
            navArgument(Screens.Categories.Const.ARG_URL, defaultStringArg()),
        )
    ) { backStackEntry ->
        ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.categoriesScreen")

        val defTitle = stringResource(R.string.browse)
        val categoryTitle by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Categories.Const.ARG_TITLE).ifEmpty { defTitle }) }

        val navController = LocalNavController.current
        BaseCategoryScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Categories.Const.ROUTE),
            topBarBlock = topBarBlock,
            defTitle = defTitle,
            categoryTitle = categoryTitle,
            parentRoute = parentRoute,
            onNavigate = { navController.navigate(it) }
        )
    }
}

// not sure if it's a good idea to have 2 composable() for one screen, not cheked if it'll work either
fun NavGraphBuilder.playerScreen(parentRoute: String, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Player(parentRoute).route,
        arguments = listOf(
            navArgument(Screens.Player.Const.ARG_PARENT_URL, defaultStringArg(isNullable = true)),
            navArgument(Screens.Player.Const.ARG_TITLE, defaultStringArg(isNullable = true)),
            navArgument(Screens.Player.Const.ARG_SUBTITLE, defaultStringArg(isNullable = true)),
            navArgument(Screens.Player.Const.ARG_IMG_URL, defaultStringArg(isNullable = true)),
            navArgument(Screens.Player.Const.ARG_URL, defaultStringArg(isNullable = true)),
            navArgument(Screens.Player.Const.ARG_ID, defaultStringArg(isNullable = true)),
            navArgument(Screens.Player.Const.ARG_IS_FAV, defaultBoolArg()),
        ),
    ) { backStackEntry ->
        ComposedTimberD("[ NavGraphBuilder.playerScreen ] ")

        val navController = LocalNavController.current

        val parentUrl by rememberSaveable { mutableStateOf(backStackEntry.getArgStrOrNull(Screens.Player.Const.ARG_PARENT_URL)) }
        parentUrl?.let {
            LoadPlayerScreen(
                viewModel = hiltViewModel(),
                isVisibleToUser = navController.isVisibleToUser(Screens.Player.Const.ROUTE),
                parentUrl = it.decodeUrl(),
                topBarBlock = topBarBlock
            )
        } ?: run {
            val stationName by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Player.Const.ARG_TITLE)) }
            val locationName by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Player.Const.ARG_SUBTITLE)) }
            val stationImgUrl by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Player.Const.ARG_IMG_URL)) }
            val rawUrl by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Player.Const.ARG_URL)) }
            val id by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Player.Const.ARG_ID)) }
            val isFav by rememberSaveable { mutableStateOf(backStackEntry.getArgBool(Screens.Player.Const.ARG_IS_FAV)) }

            PreloadedPlayerScreen(
                viewModel = hiltViewModel(),
                isVisibleToUser = navController.isVisibleToUser(Screens.Player.Const.ROUTE),
                topBarBlock = topBarBlock,
                stationName = stationName,
                locationName = locationName,
                stationImgUrl = stationImgUrl.decodeUrl(),
                rawUrl = rawUrl.decodeUrl(),
                id = id.decodeUrl(),
                isFav = isFav
            )
        }
    }
}

fun NavGraphBuilder.favoritesScreen(parentRoute: String, topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Favorites.route,
        arguments = listOf(navArgument(Screens.Favorites.Const.ARG_TITLE, defaultStringArg())),
    ) { _ ->
        ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.favoritesScreen")

        val navController = LocalNavController.current
        BaseFavoriteScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Favorites.Const.ROUTE),
            topBarBlock = topBarBlock,
            parentRoute = parentRoute,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.profileScreen(topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Profile.route,
        arguments = listOf(navArgument(Screens.Profile.Const.ARG_TITLE, defaultStringArg())),
    ) {
        ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.profileScreen")

        val navController = LocalNavController.current
        BaseProfileScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Profile.Const.ROUTE),
            topBarBlock = topBarBlock,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.settingsScreen(topBarBlock: (TopBarState) -> Unit) {
    composable(
        route = Screens.Settings.route,
        arguments = listOf(navArgument(Screens.Settings.Const.ARG_TITLE, defaultStringArg())),
    ) {
        ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.settingsScreen")

        val navController = LocalNavController.current
        BaseSettingsScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Settings.Const.ROUTE),
            topBarBlock = topBarBlock
        )
    }
}


/**
 * idk... will research later.
 * IF you'll switch BottomNav items really fast it'll cause the wrong TopBar state
 * This approach resolves the issue but doesn't look right.
 * */
private fun NavHostController.isVisibleToUser(route: String) = currentDestination?.route?.contains(route) ?: true

private fun defaultStringArg(isNullable: Boolean = false): NavArgumentBuilder.() -> Unit = {
    type = NavType.StringType
    nullable = isNullable
    defaultValue = if (isNullable) null else String.EMPTY
}

private fun defaultBoolArg(): NavArgumentBuilder.() -> Unit = {
    type = NavType.BoolType
    defaultValue = false
}

fun NavBackStackEntry.getArgStrOrNull(argName: String) = arguments?.getString(argName)
fun NavBackStackEntry.getArgStr(argName: String) = getArgStrOrNull(argName).orEmpty()
fun NavBackStackEntry.getArgBool(argName: String) = arguments?.getBoolean(argName) ?: false