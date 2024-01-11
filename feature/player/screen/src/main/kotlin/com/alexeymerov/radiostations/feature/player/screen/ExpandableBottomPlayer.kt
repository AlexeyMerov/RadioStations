package com.alexeymerov.radiostations.feature.player.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.LocalConnectionStatus
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape

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

    val isPlaying = playerState == PlayerState.PLAYING
    val isLoading = playerState == PlayerState.LOADING

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
                contentDescription = String.EMPTY
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
                contentDescription = String.EMPTY
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    strokeWidth = 2.dp,
                    color = onContainerColor,
                    strokeCap = StrokeCap.Round
                )
            } else {
                PlayButton(
                    modifier = Modifier,
                    onColor = onContainerColor,
                    isPlaying = isPlaying,
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

@Composable
private fun PlayButton(modifier: Modifier, onColor: Color, isPlaying: Boolean, onTogglePlay: () -> Unit) {
    val lotSpeed = if (isPlaying) -2f else 2f
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playstop))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        speed = lotSpeed,
        clipSpec = LottieClipSpec.Progress(max = 0.5f)
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = onColor.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    val isNetworkAvailable = LocalConnectionStatus.current
    LottieAnimation(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = onColor,
                    bounded = false,
                    radius = 20.dp
                ),
                onClick = {
                    if (isNetworkAvailable) {
                        onTogglePlay.invoke()
                    }
                }
            ),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = { animationStateProgress }
    )
}

/**
 * Calculate animation data for bottom player element position on the screen
 * Based on exand/collapse progress
 *
 * @param density - LocalDensity.current
 * @param statusBarInsets - WindowInsets.statusBars
 * @param parentHeightDp - bottomSheet height by default or LocalConfiguration.current.screenHeightDp.dp
 * @param parentWidthDp - bottomSheet width by default or LocalConfiguration.current.screenWidthDp.dp
 * @param progress - current expand/collapse progress
 * @param peekHeightDp - peek height of bottom sheet
 * @param textWidth - width of text in px
 * @param textHeight - height of text in px
 * @param iconPlayLoadingSizeDp - size of play/loading icon
 * */
private fun calculateAnimationData(
    isLandscape: Boolean,
    density: Density,
    statusBarInsets: WindowInsets,
    parentHeightDp: Dp,
    parentWidthDp: Dp,
    progress: Float,
    peekHeightDp: Dp,
    textWidth: Float,
    textHeight: Float,
    iconPlayLoadingSizeDp: Dp
): CollapseExpandData {
    return with(density) {
        val peekHeight = peekHeightDp.toPx()

        val maxWidthDp = 640.dp // default for bottomSheet
        val parentWidth = (if (parentWidthDp > maxWidthDp) maxWidthDp else parentWidthDp).toPx()
        val parentHeight = parentHeightDp.toPx()

        val parentHalfWidth = parentWidth.div(2f)
        val parentHalfHeight = parentHeight.div(2f)

        val padding16AsPx = 16.dp.toPx()
        val iconSizeDp = 48.dp
        val iconSize = iconSizeDp.toPx()

        val imgCollapsedSizeDp = 26.dp
        val imgExpandedSizeDp = if (isLandscape) 208.dp else 260.dp
        val imgCollapsedCorenres = 2.dp.toPx()
        val imgExapandedCoreners = 8.dp.toPx()
        val imgCollapsedSize = imgCollapsedSizeDp.toPx()
        val imgExpandedSize = imgExpandedSizeDp.toPx()

        val iconPlayLoadingSize = iconPlayLoadingSizeDp.toPx()

        val textMaxSize = if (isLandscape) 20.sp else 28.sp

        // Calculate status bar top offset
        val defaultStatusBarOffsetX = statusBarInsets.getTop(density).toFloat()
        val statusBarOffsetX = lerp(0f, defaultStatusBarOffsetX, progress)

        /* -- CALCULATE IMAGE -- */

        // Calculate image size, cornes, elevation
        val imgSize = lerp(imgCollapsedSizeDp, imgExpandedSizeDp, progress)
        val imgCorners = lerp(imgCollapsedCorenres, imgExapandedCoreners, progress)
        val imgElevation = lerp(0f, 16f, progress)

        val imgExapandedOffsetX = when {
            isLandscape -> parentHalfWidth.div(2f) - imgExpandedSize.div(2f) - padding16AsPx
            else -> parentHalfWidth - imgExpandedSize.div(2f) - padding16AsPx
        }
        val imgOffsetX = lerp(0f, imgExapandedOffsetX, progress)

        val imgCollapsedOffsetY = (peekHeight - imgCollapsedSize).div(2f)
        val imgExpandedOffsetY = when {
            isLandscape -> parentHalfHeight - imgExpandedSize.div(2f) - padding16AsPx
            else -> parentHalfHeight - imgExpandedSize - padding16AsPx
        }
        val imgOffsetY = lerp(imgCollapsedOffsetY, imgExpandedOffsetY, progress)

        /* -- CALCULATE TEXT -- */

        // Calculate text X offset
        val textCollapsedOffsetX = imgCollapsedSize + 8.dp.toPx()
        val textExpandedOffsetX = parentHalfWidth - textWidth.div(2f) - padding16AsPx
        val textOffsetX = lerp(textCollapsedOffsetX, textExpandedOffsetX, progress)

        // Calculate text Y offset
        val textCollapsedOffsetY = (peekHeight - textHeight).div(2f)
        val textExpandedOffsetY = imgExpandedSize + imgExpandedOffsetY + padding16AsPx.times(2f)
        val textOffsetY = lerp(textCollapsedOffsetY, textExpandedOffsetY, progress)

        // Calculate text max width
        val textCollapsedMaxWidth = parentWidthDp - imgCollapsedSizeDp - 8.dp - (iconSizeDp * 2)
        val textExpandedMaxWidth = parentWidthDp - 62.dp
        val textMaxWidth = lerp(textCollapsedMaxWidth, textExpandedMaxWidth, progress)

        // Calculate other text params
        val textSize = lerp(16.sp, textMaxSize, progress)
        val textMaxLines = lerp(1, 4, progress)

        /* -- CALCULATE ICONS -- */

        // Calculate close icon X offset
        val iconCloseOffsetX = parentWidth - iconSize - padding16AsPx

        // Calculate play/loading icon X offset
        val iconPlayLoadingCollapsedOffsetX = iconCloseOffsetX - iconPlayLoadingSize
        val iconPlayLoadingExpandedOffsetX = when {
            isLandscape -> parentHalfWidth + parentHalfWidth.div(2)
            else -> parentHalfWidth - iconPlayLoadingSize.div(2f) - padding16AsPx
        }
        val iconPlayLoadingOffsetX = lerp(iconPlayLoadingCollapsedOffsetX, iconPlayLoadingExpandedOffsetX, progress)

        // Calculate play/loading Y offset
        val iconPlayLoadingCollapsedOffsetY = (peekHeight - iconPlayLoadingSize).div(2f)
        val iconPlayLoadingExpandedOffsetY = when {
            isLandscape -> imgExpandedOffsetY + imgExpandedSize.div(2f) - padding16AsPx
            else -> textExpandedOffsetY + textHeight + padding16AsPx.times(4f)
        }
        val iconPlayLoadingOffsetY = lerp(iconPlayLoadingCollapsedOffsetY, iconPlayLoadingExpandedOffsetY, progress)

        CollapseExpandData(
            statusBarTopOffset = statusBarOffsetX,

            imgSize = imgSize,
            imgCorners = imgCorners,
            imgOffset = Offset(imgOffsetX, imgOffsetY),
            imgElevation = imgElevation,

            textOffset = Offset(textOffsetX, textOffsetY),
            textSize = textSize,
            textMaxWidth = textMaxWidth,
            textMaxLines = textMaxLines,

            iconCloseOffset = Offset(iconCloseOffsetX, 0f),
            iconPlayLoadingOffset = Offset(iconPlayLoadingOffsetX, iconPlayLoadingOffsetY),
            iconPlayLoadingScale = 1f + progress
        )
    }
}

private data class CollapseExpandData(
    val statusBarTopOffset: Float,
    val imgSize: Dp,
    val imgCorners: Float,
    val imgOffset: Offset,
    val imgElevation: Float,
    val textOffset: Offset,
    val textSize: TextUnit,
    val textMaxWidth: Dp,
    val textMaxLines: Int,
    val iconCloseOffset: Offset,
    val iconPlayLoadingOffset: Offset,
    val iconPlayLoadingScale: Float,
)
