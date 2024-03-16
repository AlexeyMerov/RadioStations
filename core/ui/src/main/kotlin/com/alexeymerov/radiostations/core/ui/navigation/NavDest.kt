package com.alexeymerov.radiostations.core.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.ui.R

sealed class Tabs(val route: String, @StringRes val stringId: Int, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Browse : Tabs(TAB_BROWSE, R.string.browse, Icons.Outlined.Category, Icons.Filled.Category)
    data object Favorites : Tabs(TAB_FAVORITES, R.string.favorites, Icons.Rounded.StarOutline, Icons.Rounded.Star)
    data object You : Tabs(TAB_YOU, R.string.you, Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle)

    private companion object {
        const val TAB_BROWSE = "tab_browse"
        const val TAB_FAVORITES = "tab_favorites"
        const val TAB_YOU = "tab_you"
    }
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
            route = "$parentRoute##${Const.ROUTE}",
            Const.ARG_TUNE_ID
        )
    ) {
        object Const {
            const val ROUTE: String = "player"
            const val ARG_TUNE_ID: String = "id"
        }

        fun createRoute(tuneId: String): String = createNewRoute(
            route = "$parentRoute##${Const.ROUTE}",
            tuneId
        )
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
private fun createBaseRoute(route: String, vararg args: Any) = route + args.joinToString(prefix = "/", separator = "/") { "{$it}" }

private fun createNewRoute(route: String, vararg args: Any?) = route + args.joinToString(prefix = "/", separator = "/") { "$it" }

// ugly workaround. compose navigation can't use links in args
// URLEncoder/Decoder brake it somehow
private const val ENCODE_SLASH = ";&!"
private const val ENCODE_QUESTION = ";&*"
private fun String.encodeUrl() = replace("/", ENCODE_SLASH).replace("?", ENCODE_QUESTION)
fun String.decodeUrl() = replace(ENCODE_SLASH, "/").replace(ENCODE_QUESTION, "?")