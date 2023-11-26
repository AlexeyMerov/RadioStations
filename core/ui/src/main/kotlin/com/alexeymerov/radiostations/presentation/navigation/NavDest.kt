package com.alexeymerov.radiostations.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.core.ui.R


sealed class Tabs(val route: String, @StringRes val stringId: Int, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Browse : Tabs("browse", R.string.browse, Icons.Outlined.Category, Icons.Filled.Category)
    data object Favorites : Tabs("favorites", R.string.favorites, Icons.Rounded.StarOutline, Icons.Rounded.Star)
    data object You : Tabs("you", R.string.you, Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle)
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

    data class Player(val parentRoute: String) : Screens(
        createBaseRoute(
            "$parentRoute##${Const.ROUTE}",
            Const.ARG_TITLE,
            Const.ARG_SUBTITLE,
            Const.ARG_IMG_URL,
            Const.ARG_URL,
            Const.ARG_ID,
            Const.ARG_IS_FAV
        )
    ) {
        object Const {
            const val ROUTE: String = "player"
            const val ARG_TITLE: String = "title"
            const val ARG_SUBTITLE: String = "subtitle"
            const val ARG_IMG_URL: String = "imgUrl"
            const val ARG_URL: String = "url"
            const val ARG_ID: String = "id"
            const val ARG_IS_FAV: String = "isFav"
        }

        fun createRoute(stationName: String, locationName: String, stationImgUrl: String, rawUrl: String, id: String, isFav: Boolean): String {
            return createNewRoute(
                route = "$parentRoute##${Const.ROUTE}",
                args = arrayOf(stationName, locationName, stationImgUrl.encodeUrl(), rawUrl.encodeUrl(), id.encodeUrl(), isFav)
            )
        }
    }

    data object Favorites : Screens(createBaseRoute(Const.ROUTE, Const.ARG_TITLE)) {
        object Const {
            const val ROUTE: String = "favorites"
            const val ARG_TITLE: String = "title"
        }
    }

    data object Profile : Screens(createBaseRoute(Const.ROUTE, Const.ARG_TITLE)) {
        object Const {
            const val ROUTE: String = "profile"
            const val ARG_TITLE: String = "title"
        }
    }

    data object Settings : Screens(createBaseRoute(Const.ROUTE, Const.ARG_TITLE)) {
        object Const {
            const val ROUTE: String = "settings"
            const val ARG_TITLE: String = "title"
        }
    }
}

//don't want to make if inside... for now will duplicate
private fun createBaseRoute(route: String, vararg args: Any) = route + args.joinToString(separator = "/") { "{$it}" }

private fun createNewRoute(route: String, vararg args: Any) = route + args.joinToString(separator = "/") { "$it" }

// ugly workaround. compose navigation can't use links in args
private fun String.encodeUrl() = replace("/", "!").replace("?", "*")
fun String.decodeUrl() = replace("!", "/").replace("*", "?")