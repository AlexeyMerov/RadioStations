package com.alexeymerov.radiostations.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation


fun NavGraphBuilder.browseGraph(navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    navigation(startDestination = Screens.Categories.route, route = Tabs.Browse.route) {
        categoriesScreen(Tabs.Browse.route, navController, topBarBlock)
        playerScreen(Tabs.Browse.route, navController, topBarBlock)
    }
}

fun NavGraphBuilder.favoriteGraph(navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    navigation(startDestination = Screens.Favorites.route, route = Tabs.Favorites.route) {
        favoritesScreen(Tabs.Favorites.route, navController, topBarBlock)
        playerScreen(Tabs.Favorites.route, navController, topBarBlock)
    }
}

fun NavGraphBuilder.youGraph(navController: NavHostController, topBarBlock: (TopBarState) -> Unit) {
    navigation(startDestination = Screens.Profile.route, route = Tabs.You.route) {
        profileScreen(navController, topBarBlock)
        settingsScreen(navController, topBarBlock)
    }
}