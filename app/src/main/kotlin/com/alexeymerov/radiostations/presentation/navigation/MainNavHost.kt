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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    var appBarState by rememberSaveable { mutableStateOf(AppBarState()) }
    val appBarBlock: @Composable (AppBarState) -> Unit = {
        CallOnLaunch { appBarState = it }
    }

    Surface {
        Scaffold(
            topBar = { CreateTopBar(appBarState, appBarState.displayBackButton, navController) },
            bottomBar = { CreateBottomBar(navController) },
            content = { paddingValues -> CreateScaffoldContent(navController, paddingValues, appBarBlock) }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CreateTopBar(barState: AppBarState, displayBackButton: Boolean, navController: NavController) {
    val titleString = barState.titleRes?.let { stringResource(it) } ?: barState.title
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent), //with color it has some delay for color animation
        title = { TopBarTitle(titleString, barState) },
        navigationIcon = { NavigationIcon(displayBackButton, navController) },
        actions = { TopBarActions(barState) }
    )
}

@Composable
private fun TopBarTitle(titleString: String, barState: AppBarState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = titleString,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = String.EMPTY
        ) { targetText ->
            Text(text = targetText, fontWeight = FontWeight.Bold)
        }
        if (barState.subTitle.isNotEmpty()) {
            AnimatedContent(
                targetState = barState.subTitle,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = String.EMPTY
            ) { targetText ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .alpha(0.7f)
                            .size(12.dp),
                        imageVector = Icons.Outlined.LocationCity,
                        contentDescription = String.EMPTY
                    )

                    Text(
                        modifier = Modifier
                            .alpha(0.7f)
                            .padding(start = 4.dp),
                        text = targetText,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationIcon(displayBackButton: Boolean, navController: NavController) {
    AnimatedVisibility(
        visible = displayBackButton,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
    }
}

@Composable
private fun RowScope.TopBarActions(barState: AppBarState) {
    AnimatedVisibility(
        visible = barState.rightIcon != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        var needShowMenu by rememberSaveable { mutableStateOf(false) }
        barState.rightIcon?.let {
            IconButton(onClick = {
                if (barState.dropDownMenu != null) {
                    needShowMenu = !needShowMenu
                }

                barState.rightIconAction?.invoke()
            }
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = String.EMPTY
                )
            }

            DropdownMenu(
                expanded = needShowMenu,
                onDismissRequest = { needShowMenu = false },
                modifier = Modifier.defaultMinSize(minWidth = 125.dp),
                offset = DpOffset(x = 12.dp, y = 0.dp),
            ) {
                barState.dropDownMenu?.invoke()
            }
        }
    }
}

@Composable
private fun CreateBottomBar(navController: NavHostController) {
    val tabs = Tabs::class::sealedSubclasses.get()

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        tabs
            .mapNotNull { it.objectInstance }
            .forEach { tab ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                BottomBarItem(tab, isSelected, navController)
            }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    tab: Tabs,
    isSelected: Boolean,
    navController: NavHostController
) {
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
            youGraph(navController, appBarBlock)
        }
    }
}

@Immutable
@Stable
@Parcelize
data class AppBarState(
    @StringRes val titleRes: Int? = null,
    val title: String = String.EMPTY,
    val subTitle: String = String.EMPTY,
    val displayBackButton: Boolean = false,
    val rightIcon: @RawValue ImageVector? = null,
    val rightIconAction: (() -> Unit)? = null,
    val dropDownMenu: (@Composable () -> Unit)? = null
) : Parcelable