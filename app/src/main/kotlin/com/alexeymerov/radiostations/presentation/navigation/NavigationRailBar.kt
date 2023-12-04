package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.presentation.MainViewModel

@Composable
fun BottomBarWithPlayer(
    navController: NavHostController,
    playerState: AudioUseCase.PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites, Tabs.You)

    Column {
        BottomPlayer(playerState, playerTitle, onPlayerAction)

        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            tabs.forEach { tab ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                BottomBarItem(navController, tab, isSelected)
            }
        }
    }
}

@Composable
fun CreateNavigationRail(
    navController: NavHostController,
) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites, Tabs.You)
    NavigationRail {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Spacer(Modifier.weight(1f))

        tabs.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationRailItem(navController, tab, isSelected)
        }

        Spacer(Modifier.weight(1f))
    }

}

@Composable
private fun RowScope.BottomBarItem(
    navController: NavHostController,
    tab: Tabs,
    isSelected: Boolean
) {
    NavigationBarItem(
        icon = { PrepareItemIcon(isSelected, tab) },
        label = { PrepaItemLabel(tab) },
        selected = isSelected,
        onClick = { onTabClick(navController, tab) }
    )
}

@Composable
private fun ColumnScope.NavigationRailItem(
    navController: NavHostController,
    tab: Tabs,
    isSelected: Boolean
) {
    NavigationRailItem(
        icon = { PrepareItemIcon(isSelected, tab) },
        label = { PrepaItemLabel(tab) },
        selected = isSelected,
        onClick = { onTabClick(navController, tab) }
    )
}

@Composable
private fun PrepareItemIcon(isSelected: Boolean, tab: Tabs) {
    val icon = if (isSelected) tab.selectedIcon else tab.icon
    Icon(icon, contentDescription = stringResource(tab.stringId))
}

@Composable
private fun PrepaItemLabel(tab: Tabs) {
    Text(stringResource(tab.stringId))
}

private fun onTabClick(navController: NavHostController, tab: Tabs) {
    navController.navigate(tab.route) {
        // Pop up to the start destination of the graph
        // to avoid building up a large stack of destinations on the back stack as users select items
        val startDestId = navController.graph.findStartDestination().id
        popUpTo(startDestId) { saveState = true }

        launchSingleTop = true
        restoreState = true
    }
}
