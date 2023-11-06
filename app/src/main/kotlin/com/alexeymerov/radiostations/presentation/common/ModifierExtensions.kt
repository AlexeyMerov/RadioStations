package com.alexeymerov.radiostations.presentation.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

@Suppress("InfiniteTransitionLabel", "InfinitePropertiesLabel")
fun Modifier.shimmerEffect(shape: Shape = RectangleShape): Modifier = composed {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.outlineVariant,
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.outlineVariant,
    )

    var size by remember { mutableStateOf(IntSize.Zero) }
    val widthFloat = size.width.toFloat()
    val halfWidthFloat = widthFloat / 2

    val startOffsetX by rememberInfiniteTransition().animateFloat(
        initialValue = -widthFloat,
        targetValue = widthFloat,
        animationSpec = infiniteRepeatable(
            animation = tween(2000)
        )
    )

    val brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(x = startOffsetX, y = startOffsetX),
        end = Offset(x = startOffsetX + widthFloat, y = startOffsetX + halfWidthFloat)
    )

    this
        .background(brush, shape)
        .onGloballyPositioned { size = it.size }
}