package com.alexeymerov.radiostations.core.ui.coil.transformers


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil.size.Size
import coil.transform.Transformation

/**
 * Rect images -> square images
 * */
class SquarifyTransformation : Transformation {

    override val cacheKey: String = javaClass.name
    override fun equals(other: Any?) = other is SquarifyTransformation
    override fun hashCode() = javaClass.hashCode()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val resultSize = maxOf(input.width, input.height)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.DST_ATOP)
        }

        val leftOffset = when {
            input.width < resultSize -> (resultSize - input.width) / 2f
            else -> 0f
        }

        val topOffset = when {
            input.height < resultSize -> (resultSize - input.height) / 2f
            else -> 0f
        }

        return createBitmap(resultSize, resultSize, input.config).applyCanvas {
            drawBitmap(input, leftOffset, topOffset, paint)
        }
    }
}