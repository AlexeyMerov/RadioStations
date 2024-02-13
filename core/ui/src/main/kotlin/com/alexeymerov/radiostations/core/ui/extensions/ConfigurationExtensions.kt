package com.alexeymerov.radiostations.core.ui.extensions

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes
fun Configuration.isTablet(): Boolean {
    return if (isLandscape()) {
        screenWidthDp > 900 // my phone is 866, not in the range apparently
    } else {
        screenWidthDp > 600
    }
}

fun Configuration.isPortrait() = orientation == Configuration.ORIENTATION_PORTRAIT

fun Configuration.isLandscape() = orientation == Configuration.ORIENTATION_LANDSCAPE

fun Configuration.maxDialogHeight() = screenHeightDp.dp - 56.dp

fun Configuration.maxDialogWidth() = screenWidthDp.dp - 56.dp

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }