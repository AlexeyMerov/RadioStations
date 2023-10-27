package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation


fun NavGraphBuilder.browseGraph(navController: NavHostController, appBarBlock: @Composable (AppBarState) -> Unit) {
    navigation(startDestination = Screens.Categories.route, route = Tabs.Browse.route) {
        categoriesScreen(Tabs.Browse.route, navController, appBarBlock)
        playerScreen(Tabs.Browse.route, appBarBlock)
    }
}

fun NavGraphBuilder.favoriteGraph(navController: NavHostController, appBarBlock: @Composable (AppBarState) -> Unit) {
    navigation(startDestination = Screens.Favorites.route, route = Tabs.Favorites.route) {
        favoritesScreen(Tabs.Favorites.route, navController, appBarBlock)
        playerScreen(Tabs.Favorites.route, appBarBlock)
    }
}

fun NavGraphBuilder.youGraph(navController: NavHostController, appBarBlock: @Composable (AppBarState) -> Unit) {
    navigation(startDestination = Screens.Profile.route, route = Tabs.You.route) {
        profileScreen(navController, appBarBlock)
    }
}