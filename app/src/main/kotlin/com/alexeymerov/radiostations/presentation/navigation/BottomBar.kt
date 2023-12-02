package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.presentation.MainViewModel

@Composable
fun BottomBar(
    playerState: AudioUseCase.PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites, Tabs.You)

    Column {
        BottomPlayer(playerState, playerTitle, onPlayerAction)

        NavigationBar {
            val navBackStackEntry by LocalNavController.current.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            tabs.forEach { tab ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                BottomBarItem(tab, isSelected)
            }
        }
    }

}

@Composable
private fun RowScope.BottomBarItem(
    tab: Tabs,
    isSelected: Boolean
) {
    val navController = LocalNavController.current
    val icon = if (isSelected) tab.selectedIcon else tab.icon
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = stringResource(tab.stringId)) },
        label = { Text(stringResource(tab.stringId)) },
        selected = isSelected,
        onClick = {
            navController.navigate(tab.route) {
                // Pop up to the start destination of the graph
                // to avoid building up a large stack of destinations on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }
        }
    )
}