package com.alexeymerov.radiostations.presentation.navigation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.CallOnLaunch
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import kotlinx.parcelize.Parcelize


@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    var scaffoldViewState by rememberSaveable { mutableStateOf(AppBarState()) }
    val appBarBlock: @Composable (AppBarState) -> Unit = {
        CallOnLaunch { scaffoldViewState = it }
    }

    StationsAppTheme {
        Surface {
            Scaffold(
                topBar = { CreateTopBar(scaffoldViewState, scaffoldViewState.displayBackButton, navController) },
                bottomBar = { CreateBottomBar(navController) },
                content = { paddingValues -> CreateScaffoldContent(navController, paddingValues, appBarBlock) }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CreateTopBar(viewState: AppBarState, displayBackButton: Boolean, navController: NavController) {
    val titleString = viewState.titleRes?.let { stringResource(it) } ?: viewState.title
    val categoryTitle by rememberSaveable(viewState) { mutableStateOf(titleString) }

    Surface {
        CenterAlignedTopAppBar(
            title = {
                AnimatedContent(
                    targetState = categoryTitle,
                    transitionSpec = {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    },
                    label = String.EMPTY
                ) { targetText ->
                    Text(text = targetText, fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                AnimatedVisibility(
                    visible = displayBackButton,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun CreateBottomBar(navController: NavHostController) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        tabs.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
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
    }
}

@Composable
private fun CreateScaffoldContent(
    navController: NavHostController,
    paddingValues: PaddingValues,
    appBarBlock: @Composable (AppBarState) -> Unit
) {
    Surface(Modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = Tabs.Browse.route,
            enterTransition = {
                slideIntoContainer(SlideDirection.Left, spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(SlideDirection.Left, spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            },
            popEnterTransition = {
                slideIntoContainer(SlideDirection.Right, spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn()
            },
            popExitTransition = {
                slideOutOfContainer(SlideDirection.Right, spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            }
        ) {
            browseGraph(navController, appBarBlock)
            favoriteGraph(navController, appBarBlock)
        }
    }
}

@Immutable
@Stable
@Parcelize
data class AppBarState(
    @StringRes val titleRes: Int? = null,
    val title: String = String.EMPTY,
    val displayBackButton: Boolean = false
) : Parcelable