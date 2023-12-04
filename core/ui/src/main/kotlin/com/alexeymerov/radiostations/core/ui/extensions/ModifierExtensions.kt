package com.alexeymerov.radiostations.core.ui.extensions

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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.offset

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

fun Modifier.ignoreWidthConstrains(dp: Dp) = layout { measurable, constraints ->

    val padding = -dp
    // Measure the composable adding the side padding*2 (left+right)
    val placeable = measurable.measure(
        constraints.offset(horizontal = -padding.roundToPx() * 2)
    )

    //increase the width adding the side padding*2
    layout(
        width = placeable.width + padding.roundToPx() * 2,
        height = placeable.height
    ) {
        // Where the composable gets placed
        placeable.place(+padding.roundToPx(), 0)
    }
}