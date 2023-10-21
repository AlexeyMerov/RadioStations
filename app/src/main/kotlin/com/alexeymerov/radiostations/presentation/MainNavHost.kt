package com.alexeymerov.radiostations.presentation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.presentation.common.CallOnLaunch
import com.alexeymerov.radiostations.presentation.screen.category.CategoryListScreen
import com.alexeymerov.radiostations.presentation.screen.player.PlayerScreen
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import kotlinx.parcelize.Parcelize
import timber.log.Timber


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
                content = { paddingValues ->
                    Surface {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Categories.route,
                            modifier = Modifier.padding(paddingValues),
                            enterTransition = {
                                slideIntoContainer(slideLeftDirection, transitionAnimationSpec) + fadeIn()
                            },
                            exitTransition = {
                                slideOutOfContainer(slideLeftDirection, transitionAnimationSpec) + fadeOut()
                            },
                            popEnterTransition = {
                                slideIntoContainer(slideRightDirection, transitionAnimationSpec) + fadeIn()
                            },
                            popExitTransition = {
                                slideOutOfContainer(slideRightDirection, transitionAnimationSpec) + fadeOut()
                            }) {
                            categoriesScreen(navController, appBarBlock)
                            playerScreen(appBarBlock)
                        }
                    }
                }
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
            title = { Text(categoryTitle, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                if (!displayBackButton) return@CenterAlignedTopAppBar
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )
    }
}

private fun NavGraphBuilder.categoriesScreen(navController: NavController, appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Categories.route,
        arguments = createListOfStringArgs(NavDest.Category.ARG_TITLE, NavDest.Category.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] NavGraphBuilder.categoriesScreen")

        val defTitle = LocalContext.current.getString(R.string.browse)
        val categoryTitle by rememberSaveable { mutableStateOf(backStackEntry.getArg(NavDest.Category.ARG_TITLE).ifEmpty { defTitle }) }
        val displayBackButton by rememberSaveable(categoryTitle) { mutableStateOf(categoryTitle != defTitle) }
        appBarBlock.invoke(AppBarState(title = categoryTitle, displayBackButton = displayBackButton))
        CategoryListScreen(
            onCategoryClick = { navController.navigate(Screens.Categories.createRoute(it.text, it.url)) },
            onAudioClick = { navController.navigate(Screens.Player.createRoute(it.text, it.image.orEmpty(), it.url)) }
        )
    }
}

private fun NavGraphBuilder.playerScreen(appBarBlock: @Composable (AppBarState) -> Unit) {
    composable(
        route = Screens.Player.route,
        arguments = createListOfStringArgs(NavDest.Player.ARG_TITLE, NavDest.Player.ARG_IMG_URL, NavDest.Player.ARG_URL),
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

        val stationName by rememberSaveable { mutableStateOf(backStackEntry.getArg(NavDest.Player.ARG_TITLE)) }
        val stationImgUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(NavDest.Player.ARG_IMG_URL)) }
        val rawUrl by rememberSaveable { mutableStateOf(backStackEntry.getArg(NavDest.Player.ARG_URL)) }
        appBarBlock.invoke(AppBarState(title = stationName))
        PlayerScreen(stationImgUrl.decodeUrl(), rawUrl.decodeUrl())
    }
}

sealed class Screens(val route: String) {
    data object Categories : Screens(createBaseRoute(NavDest.Category.ROUTE, NavDest.Category.ARG_TITLE, NavDest.Category.ARG_URL)) {
        fun createRoute(categoryTitle: String = String.EMPTY, categoryUrl: String = String.EMPTY): String {
            return createNewRoute(NavDest.Category.ROUTE, categoryTitle, categoryUrl.encodeUrl())

        }
    }

    data object Player :
        Screens(createBaseRoute(NavDest.Player.ROUTE, NavDest.Player.ARG_TITLE, NavDest.Player.ARG_IMG_URL, NavDest.Player.ARG_URL)) {
        fun createRoute(stationName: String, stationImgUrl: String, rawUrl: String): String {
            return createNewRoute(NavDest.Player.ROUTE, stationName, stationImgUrl.encodeUrl(), rawUrl.encodeUrl())
        }
    }
}

sealed interface NavDest {
    data object Category : NavDest {
        const val ROUTE: String = "categories"
        const val ARG_TITLE: String = "title"
        const val ARG_URL: String = "url"
    }

    data object Player : NavDest {
        const val ROUTE: String = "player"
        const val ARG_TITLE: String = "title"
        const val ARG_IMG_URL: String = "imgUrl"
        const val ARG_URL: String = "url"
    }
}

private fun NavBackStackEntry.getArg(arg: String) = arguments?.getString(arg).orEmpty()

private fun createListOfStringArgs(vararg args: String) = args.map { navArgument(it, defaultStringArg()) }

private fun defaultStringArg(): NavArgumentBuilder.() -> Unit = {
    type = NavType.StringType
    defaultValue = String.EMPTY
}

//don't want to make if inside... for now will duplicate
private fun createBaseRoute(route: String, vararg args: String) = route + args.joinToString { "/{$it}" }

private fun createNewRoute(route: String, vararg args: String) = route + args.joinToString { "/$it" }

// ugly workaround. compose navigation can't use links in args
private fun String.encodeUrl() = replace("/", "!").replace("?", "*")
fun String.decodeUrl() = replace("!", "/").replace("*", "?")

private val slideLeftDirection = AnimatedContentTransitionScope.SlideDirection.Left
private val slideRightDirection = AnimatedContentTransitionScope.SlideDirection.Right
private val transitionAnimationSpec = tween<IntOffset>(300)

@Immutable
@Stable
@Parcelize
data class AppBarState(
    @StringRes val titleRes: Int? = null,
    val title: String = String.EMPTY,
    val displayBackButton: Boolean = true
) : Parcelable













