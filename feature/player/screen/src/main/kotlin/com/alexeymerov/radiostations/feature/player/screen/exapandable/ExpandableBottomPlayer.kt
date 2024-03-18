package com.alexeymerov.radiostations.feature.player.screen.exapandable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.feature.player.screen.elements.PlayButton
import com.alexeymerov.radiostations.feature.player.screen.elements.PlayerArtwork

@Composable
fun ExpandableBottomPlayer(
    modifier: Modifier,
    peekHeightDp: Dp,
    progress: Float,
    containerColor: Color,
    onContainerColor: Color,
    playerState: PlayerState,
    currentMedia: AudioItemDto?,
    onCloseAction: () -> Unit,
    onToggleAudio: () -> Unit,
    onCollapse: () -> Unit
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val statusBarInsets = WindowInsets.statusBars

    val iconPlayLoadingSize = 36.dp
    var textHeight by remember { mutableFloatStateOf(0f) }
    var textWidth by remember { mutableFloatStateOf(0f) }

    val animationData by remember(progress, textHeight, textWidth) {
        derivedStateOf {
            calculateAnimationData(
                isLandscape = config.isLandscape(),
                density = density,
                statusBarInsets = statusBarInsets,
                parentHeightDp = config.screenHeightDp.dp,
                parentWidthDp = config.screenWidthDp.dp,
                progress = progress,
                peekHeightDp = peekHeightDp,
                textWidth = textWidth,
                textHeight = textHeight,
                iconPlayLoadingSizeDp = iconPlayLoadingSize
            )
        }
    }

    Box(
        modifier = modifier
            .background(containerColor)
            .padding(horizontal = 16.dp)
            .graphicsLayer(translationY = animationData.statusBarTopOffset)
    ) {

        PlayerArtwork(
            modifier = Modifier
                .graphicsLayer(
                    translationX = animationData.imgOffset.x,
                    translationY = animationData.imgOffset.y,
                    clip = true,
                    shape = RoundedCornerShape(animationData.imgCorners),
                    shadowElevation = animationData.imgElevation
                )
                .size(animationData.imgSize),
            imageUrl = currentMedia?.image.orEmpty()
        )

        Text(
            modifier = Modifier
                .onGloballyPositioned {
                    textHeight = it.size.height.toFloat()
                    textWidth = it.size.width.toFloat()
                }
                .graphicsLayer(
                    translationX = animationData.textOffset.x,
                    translationY = animationData.textOffset.y,
                )
                .sizeIn(maxWidth = animationData.textMaxWidth),
            text = currentMedia?.title.orEmpty(),
            textAlign = TextAlign.Center,
            color = onContainerColor,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = animationData.textMaxLines,
            lineHeight = animationData.textSize,
            fontSize = animationData.textSize
        )

        IconButton(
            modifier = Modifier
                .graphicsLayer(alpha = progress)
                .offset(x = 16.dp.unaryMinus()),
            onClick = { onCollapse.invoke() }
        ) {
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                tint = onContainerColor,
                contentDescription = null
            )
        }

        IconButton(
            modifier = Modifier.graphicsLayer(
                translationX = animationData.iconCloseOffset.x,
            ),
            onClick = { onCloseAction.invoke() }
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                tint = onContainerColor,
                contentDescription = null
            )
        }

        Box(
            modifier = Modifier
                .graphicsLayer(
                    translationX = animationData.iconPlayLoadingOffset.x,
                    translationY = animationData.iconPlayLoadingOffset.y,
                    scaleX = animationData.iconPlayLoadingScale,
                    scaleY = animationData.iconPlayLoadingScale
                )
                .size(iconPlayLoadingSize)
        ) {
            if (playerState is PlayerState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    strokeWidth = 2.dp,
                    color = onContainerColor,
                    strokeCap = StrokeCap.Round
                )
            } else {
                PlayButton(
                    modifier = Modifier,
                    iconColor = onContainerColor,
                    radius = 20.dp,
                    isPlaying = playerState is PlayerState.Playing,
                    onTogglePlay = { onToggleAudio.invoke() }
                )
            }
        }
    }
}

@Composable
private fun PlayingWaves(modifier: Modifier, isPlaying: Boolean) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playing))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE,
        isPlaying = isPlaying,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSecondary.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        modifier = modifier,
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = {
            if (isPlaying) animationStateProgress else 0f
        },
    )
}