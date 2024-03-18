package com.alexeymerov.radiostations.feature.player.screen.exapandable

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp

internal data class CollapseExpandData(
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

/**
 * Calculate animation data for bottom player element position on the screen
 * Based on expand/collapse progress
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
internal fun calculateAnimationData(
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
        val imgCollapsedCorners = 2.dp.toPx()
        val imgExpandedCorners = 8.dp.toPx()
        val imgCollapsedSize = imgCollapsedSizeDp.toPx()
        val imgExpandedSize = imgExpandedSizeDp.toPx()

        val iconPlayLoadingSize = iconPlayLoadingSizeDp.toPx()

        val textMaxSize = if (isLandscape) 20.sp else 28.sp

        // Calculate status bar top offset
        val defaultStatusBarOffsetX = statusBarInsets.getTop(density).toFloat()
        val statusBarOffsetX = lerp(0f, defaultStatusBarOffsetX, progress)

        /* -- CALCULATE IMAGE -- */

        // Calculate image size, corners, elevation
        val imgSize = lerp(imgCollapsedSizeDp, imgExpandedSizeDp, progress)
        val imgCorners = lerp(imgCollapsedCorners, imgExpandedCorners, progress)
        val imgElevation = lerp(0f, 16f, progress)

        val imgExpandedOffsetX = when {
            isLandscape -> parentHalfWidth.div(2f) - imgExpandedSize.div(2f) - padding16AsPx
            else -> parentHalfWidth - imgExpandedSize.div(2f) - padding16AsPx
        }
        val imgOffsetX = lerp(0f, imgExpandedOffsetX, progress)

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