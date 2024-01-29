package com.alexeymerov.radiostations.presentation.navigation

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
import com.alexeymerov.radiostations.core.ui.navigation.Tabs

@Composable
fun CreateBottomBar(
    modifier: Modifier,
    navController: NavHostController,
) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites, Tabs.You)

    NavigationBar(modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        tabs.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationBarItem(
                icon = { PrepareItemIcon(isSelected, tab) },
                label = { PrepaItemLabel(tab) },
                selected = isSelected,
                onClick = { onTabClick(navController, tab) }
            )
        }
    }
}

@Composable
fun CreateNavigationRail(
    modifier: Modifier,
    navController: NavHostController,
) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites, Tabs.You)
    NavigationRail(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Spacer(Modifier.weight(1f))

        tabs.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationRailItem(
                icon = { PrepareItemIcon(isSelected, tab) },
                label = { PrepaItemLabel(tab) },
                selected = isSelected,
                onClick = { onTabClick(navController, tab) }
            )
        }

        Spacer(Modifier.weight(1f))
    }

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
