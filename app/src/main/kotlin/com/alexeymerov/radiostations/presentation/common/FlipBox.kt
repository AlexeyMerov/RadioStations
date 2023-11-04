package com.alexeymerov.radiostations.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun FlipBox(
    modifier: Modifier,
    isFlipped: Boolean = false,
    frontSide: @Composable () -> Unit,
    backSide: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        )
    )

    Box(modifier = modifier
        .graphicsLayer {
            rotationY = rotation
            cameraDistance = 10f * density
        }
    ) {
        if (rotation <= 90f) {
            frontSide.invoke()
        } else {
            Box(
                modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    },
            ) {
                backSide.invoke()
            }
        }
    }
}