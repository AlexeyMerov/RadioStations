package com.alexeymerov.radiostations.core.ui.extensions

import android.content.res.Configuration
import androidx.compose.ui.unit.dp

fun Configuration.isTablet(): Boolean {
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        screenWidthDp > 840
    } else {
        screenWidthDp > 600
    }
}

fun Configuration.isPortrait() = orientation == Configuration.ORIENTATION_PORTRAIT

fun Configuration.isLandscape() = orientation == Configuration.ORIENTATION_LANDSCAPE

fun Configuration.maxDialogHeight() = screenHeightDp.dp - 56.dp

fun Configuration.maxDialogWidth() = screenWidthDp.dp - 56.dp