package com.alexeymerov.radiostations.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation


fun NavGraphBuilder.browseGraph(topBarBlock: (TopBarState) -> Unit) {
    navigation(startDestination = Screens.Categories.route, route = Tabs.Browse.route) {
        categoriesScreen(Tabs.Browse.route, topBarBlock)
        playerScreen(Tabs.Browse.route, topBarBlock)
    }
}

fun NavGraphBuilder.favoriteGraph(topBarBlock: (TopBarState) -> Unit) {
    navigation(startDestination = Screens.Favorites.route, route = Tabs.Favorites.route) {
        favoritesScreen(Tabs.Favorites.route, topBarBlock)
        playerScreen(Tabs.Favorites.route, topBarBlock)
    }
}

fun NavGraphBuilder.youGraph(topBarBlock: (TopBarState) -> Unit) {
    navigation(startDestination = Screens.Profile.route, route = Tabs.You.route) {
        profileScreen(topBarBlock)
        settingsScreen(topBarBlock)
    }
}