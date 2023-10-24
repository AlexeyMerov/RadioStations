package com.alexeymerov.radiostations.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY


sealed class Tabs(val route: String, @StringRes val stringId: Int, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Browse : Tabs("browse", R.string.browse, Icons.Outlined.Category, Icons.Filled.Category)
    data object Favorites : Tabs("favorites", R.string.favorites, Icons.Outlined.StarOutline, Icons.Filled.Star)
}

sealed class Screens(val route: String) {
    data object Categories : Screens(createBaseRoute(Const.ROUTE, Const.ARG_TITLE, Const.ARG_URL)) {
        object Const {
            const val ROUTE: String = "categories"
            const val ARG_TITLE: String = "title"
            const val ARG_URL: String = "url"
        }

        fun createRoute(categoryTitle: String = String.EMPTY, categoryUrl: String = String.EMPTY): String {
            return createNewRoute(Const.ROUTE, categoryTitle, categoryUrl.encodeUrl())
        }
    }

    data class Player(val parentRoute: String) :
        Screens(createBaseRoute("$parentRoute##${Const.ROUTE}", Const.ARG_TITLE, Const.ARG_IMG_URL, Const.ARG_URL)) {
        object Const {
            const val ROUTE: String = "player"
            const val ARG_TITLE: String = "title"
            const val ARG_IMG_URL: String = "imgUrl"
            const val ARG_URL: String = "url"
        }

        fun createRoute(stationName: String, stationImgUrl: String, rawUrl: String): String {
            return createNewRoute("$parentRoute##${Const.ROUTE}", stationName, stationImgUrl.encodeUrl(), rawUrl.encodeUrl())
        }
    }

    data object Favorites : Screens(createBaseRoute(Const.ROUTE, Const.ARG_TITLE)) {
        object Const {
            const val ROUTE: String = "favorites"
            const val ARG_TITLE: String = "title"
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