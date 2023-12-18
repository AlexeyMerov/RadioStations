package com.alexeymerov.radiostations.core.ui.extensions

import androidx.compose.ui.graphics.Color

fun Color.lerp(other: Color, fraction: Float): Color {
    return androidx.compose.ui.graphics.lerp(this, other, fraction)
}