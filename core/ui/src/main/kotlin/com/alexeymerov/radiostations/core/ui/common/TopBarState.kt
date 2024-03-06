package com.alexeymerov.radiostations.core.ui.common

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.alexeymerov.radiostations.core.ui.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Immutable
@Stable
@Parcelize
data class TopBarState(
    val title: String,
    val subTitle: String? = null,
    val displayBackButton: Boolean = false,
    val rightIcon: RightIconItem? = null,
    val selectedItems: Int = 0
) : Parcelable

@Parcelize
data class RightIconItem(val icon: TopBarIcon) : Parcelable {
    @IgnoredOnParcel
    var action: () -> Unit = {}

    @IgnoredOnParcel
    var dropDownMenu: List<DropDownItem>? = null
}

@Parcelize
data class DropDownItem(val iconId: Int, val stringId: Int) : Parcelable {
    @IgnoredOnParcel
    var action: () -> Unit = {}
}

// doesn't look ok and mb whole TopBar state needs another approach
enum class TopBarIcon(val iconVector: ImageVector, val iconResId: Int? = null) {
    SETTINGS(Icons.Rounded.Settings, R.drawable.icon_settings),
    STAR_HALF(Icons.AutoMirrored.Rounded.StarHalf),
    STAR(Icons.Rounded.Star),
    STAR_OUTLINE(Icons.Rounded.StarOutline),
}