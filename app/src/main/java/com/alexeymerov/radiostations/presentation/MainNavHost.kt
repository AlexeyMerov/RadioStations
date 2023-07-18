@file:OptIn(ExperimentalAnimationApi::class)

package com.alexeymerov.radiostations.presentation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListScreen
import com.alexeymerov.radiostations.presentation.fragment.item.AudioScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import timber.log.Timber


@Composable
fun MainNavGraph() {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(navController, startDestination = Screens.Categories.route) {
        categoriesScreen(navController)
        audioScreen(navController)
    }
}

private fun NavGraphBuilder.categoriesScreen(navController: NavHostController) {
    composable(
        route = Screens.Categories.route,
        arguments = createListOfStringArgs(NavConst.ARG_CATEGORY_TITLE, NavConst.ARG_CATEGORY_URL),
        enterTransition = { slideIntoContainer(slideLeft, animationSpec) },
        exitTransition = { slideOutOfContainer(slideLeft, animationSpec) },
        popEnterTransition = { slideIntoContainer(slideRight, animationSpec) },
        popExitTransition = { slideOutOfContainer(slideRight, animationSpec) }
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

        val categoryTitle = backStackEntry.arguments?.getString(NavConst.ARG_CATEGORY_TITLE).orEmpty().ifEmpty { NavConst.CATEGORY_DEF_TITLE }
        val categoryUrl = backStackEntry.arguments?.getString(NavConst.ARG_CATEGORY_URL).orEmpty()
        val displayBackButton = categoryTitle != NavConst.CATEGORY_DEF_TITLE
        CategoryListScreen(navController, displayBackButton, categoryTitle, categoryUrl.decodeUrl())
    }
}

private fun NavGraphBuilder.audioScreen(navController: NavHostController) {
    composable(
        route = Screens.Audio.route,
        arguments = createListOfStringArgs(NavConst.ARG_STATION_NAME, NavConst.ARG_STATION_IMG_URL, NavConst.ARG_RAW_URL),
        enterTransition = { slideIntoContainer(slideLeft, animationSpec) },
        exitTransition = { slideOutOfContainer(slideLeft, animationSpec) },
        popEnterTransition = { slideIntoContainer(slideRight, animationSpec) },
        popExitTransition = { slideOutOfContainer(slideRight, animationSpec) }
    ) { backStackEntry ->
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

        val stationName = backStackEntry.arguments?.getString(NavConst.ARG_STATION_NAME).orEmpty()
        val stationImgUrl = backStackEntry.arguments?.getString(NavConst.ARG_STATION_IMG_URL).orEmpty()
        val rawUrl = backStackEntry.arguments?.getString(NavConst.ARG_RAW_URL).orEmpty()
        AudioScreen(navController, stationName, stationImgUrl.decodeUrl(), rawUrl.decodeUrl())
    }
}

private fun createListOfStringArgs(vararg args: String) = args.map { navArgument(it, defaultStringArg()) }

private fun defaultStringArg(): NavArgumentBuilder.() -> Unit = {
    type = NavType.StringType
    defaultValue = ""
}

//don't want to make if inside... for now will duplicate
private fun createBaseRoute(route: String, vararg args: String) = route + args.joinToString { "/{$it}" }
private fun createNewRoute(route: String, vararg args: String) = route + args.joinToString { "/$it" }

sealed class Screens(val route: String) {
    object Categories : Screens(createBaseRoute(NavConst.CATEGORY_ROUTE, NavConst.ARG_CATEGORY_TITLE, NavConst.ARG_CATEGORY_URL)) {
        fun createRoute(categoryTitle: String = "", categoryUrl: String = ""): String {
            return createNewRoute(NavConst.CATEGORY_ROUTE, categoryTitle, categoryUrl.encodeUrl())
        }
    }

    object Audio : Screens(createBaseRoute(NavConst.AUDIO_ROUTE, NavConst.ARG_STATION_NAME, NavConst.ARG_STATION_IMG_URL, NavConst.ARG_RAW_URL)) {
        fun createRoute(stationName: String, stationImgUrl: String, rawUrl: String): String {
            return createNewRoute(NavConst.AUDIO_ROUTE, stationName, stationImgUrl.encodeUrl(), rawUrl.encodeUrl())
        }
    }
}

object NavConst { // looks ugly. replace with more clean solution
    const val CATEGORY_DEF_TITLE = "Browse" // meh... string res later
    const val CATEGORY_ROUTE = "categories"
    const val ARG_CATEGORY_TITLE = "categoryTitle"
    const val ARG_CATEGORY_URL = "categoryUrl"

    const val AUDIO_ROUTE = "audio"
    const val ARG_STATION_NAME = "stationName"
    const val ARG_STATION_IMG_URL = "stationImgUrl"
    const val ARG_RAW_URL = "rawUrl"
}

// ugly workaround. compose navigation can't use links in args
fun String.encodeUrl() = replace("/", "!").replace("?", "*")
fun String.decodeUrl() = replace("!", "/").replace("*", "?")

val slideLeft = AnimatedContentScope.SlideDirection.Left
val slideRight = AnimatedContentScope.SlideDirection.Right
val animationSpec = tween<IntOffset>(200)























