package com.alexeymerov.radiostations.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.Tabs


fun NavGraphBuilder.browseGraph() {
    navigation(startDestination = Screens.Categories.route, route = Tabs.Browse.route) {
        categoriesScreen()
        playerScreen(Tabs.Browse.route)
    }
}

fun NavGraphBuilder.favoriteGraph() {
    navigation(startDestination = Screens.Favorites.route, route = Tabs.Favorites.route) {
        favoritesScreen()
        playerScreen(Tabs.Favorites.route)
    }
}

fun NavGraphBuilder.youGraph() {
    navigation(startDestination = Screens.Profile.route, route = Tabs.You.route) {
        profileScreen()
        settingsScreen()
    }
}