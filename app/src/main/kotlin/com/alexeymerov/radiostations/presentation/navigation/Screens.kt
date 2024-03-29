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
import androidx.navigation.navDeepLink
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.ProjectConst.DEEP_LINK_SCREEN_PATTERN
import com.alexeymerov.radiostations.core.common.ProjectConst.DEEP_LINK_TUNE_PATTERN
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.feature.category.BaseCategoryScreen
import com.alexeymerov.radiostations.feature.favorite.BaseFavoriteScreen
import com.alexeymerov.radiostations.feature.player.screen.BasePlayerScreen
import com.alexeymerov.radiostations.feature.profile.BaseProfileScreen
import com.alexeymerov.radiostations.feature.settings.BaseSettingsScreen


fun NavGraphBuilder.categoriesScreen() {
    composable(
        route = Screens.Categories.route,
        arguments = listOf(
            navArgument(Screens.Categories.Const.ARG_TITLE, defaultStringArg()),
            navArgument(Screens.Categories.Const.ARG_URL, defaultStringArg()),
        )
    ) { backStackEntry ->
        ComposedTimberD("NavGraphBuilder.categoriesScreen")

        val defTitle = stringResource(R.string.browse)
        val categoryTitle by rememberSaveable { mutableStateOf(backStackEntry.getArgStr(Screens.Categories.Const.ARG_TITLE).ifEmpty { defTitle }) }

        val navController = LocalNavController.current

        BaseCategoryScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Categories.Const.ROUTE),
            defTitle = defTitle,
            categoryTitle = categoryTitle,
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.playerScreen(parentRoute: String) {
    composable(
        route = Screens.Player(parentRoute).route,
        arguments = listOf(navArgument(Screens.Player.Const.ARG_TUNE_ID, defaultStringArg())),
        deepLinks = listOf(
            navDeepLink { uriPattern = "$DEEP_LINK_TUNE_PATTERN/{${Screens.Player.Const.ARG_TUNE_ID}}" }
        )
    ) {
        ComposedTimberD("[ NavGraphBuilder.playerScreen ] ")

        val navController = LocalNavController.current

        BasePlayerScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Player.Const.ROUTE)
        )
    }
}

fun NavGraphBuilder.favoritesScreen() {
    composable(
        route = Screens.Favorites.route,
        arguments = listOf(navArgument(Screens.Favorites.Const.ARG_TITLE, defaultStringArg())),
        deepLinks = listOf(
            navDeepLink { uriPattern = "$DEEP_LINK_SCREEN_PATTERN/${Screens.Favorites.Const.ROUTE}" }
        )
    ) {
        ComposedTimberD("NavGraphBuilder.favoritesScreen")

        val navController = LocalNavController.current
        BaseFavoriteScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Favorites.Const.ROUTE),
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.profileScreen() {
    composable(
        route = Screens.Profile.route,
        arguments = listOf(navArgument(Screens.Profile.Const.ARG_TITLE, defaultStringArg())),
    ) {
        ComposedTimberD("NavGraphBuilder.profileScreen")

        val navController = LocalNavController.current
        BaseProfileScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Profile.Const.ROUTE),
            onNavigate = { navController.navigate(it) }
        )
    }
}

fun NavGraphBuilder.settingsScreen() {
    composable(
        route = Screens.Settings.route,
        arguments = listOf(navArgument(Screens.Settings.Const.ARG_TITLE, defaultStringArg())),
    ) {
        ComposedTimberD("NavGraphBuilder.settingsScreen")

        val navController = LocalNavController.current
        BaseSettingsScreen(
            viewModel = hiltViewModel(),
            isVisibleToUser = navController.isVisibleToUser(Screens.Settings.Const.ROUTE),
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

fun NavBackStackEntry.getArgStrOrNull(argName: String) = arguments?.getString(argName)
fun NavBackStackEntry.getArgStr(argName: String) = getArgStrOrNull(argName).orEmpty()
fun NavBackStackEntry.getArgBool(argName: String) = arguments?.getBoolean(argName) ?: false