package com.alexeymerov.radiostations.presentation.navigation

import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alexeymerov.radiostations.common.EMPTY

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

//don't want to make if inside... for now will duplicate
private fun createBaseRoute(route: String, vararg args: String) = route + args.joinToString { "/{$it}" }

private fun createNewRoute(route: String, vararg args: String) = route + args.joinToString { "/$it" }

// ugly workaround. compose navigation can't use links in args
private fun String.encodeUrl() = replace("/", "!").replace("?", "*")
fun String.decodeUrl() = replace("!", "/").replace("*", "?")

fun createListOfStringArgs(vararg args: String) = args.map { navArgument(it, defaultStringArg()) }

private fun defaultStringArg(): NavArgumentBuilder.() -> Unit = {
    type = NavType.StringType
    defaultValue = String.EMPTY
}

fun NavBackStackEntry.getArg(arg: String) = arguments?.getString(arg).orEmpty()