package com.alexeymerov.radiostations.core.ui.coil.transformers


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil.size.Size
import coil.transform.Transformation

/**
 * Modified version to avoid transparent backgrounds
 *
 * @see coil.transform.CircleCropTransformation
 * */
class MarkerTransformation : Transformation {

    override val cacheKey: String = javaClass.name
    override fun equals(other: Any?) = other is MarkerTransformation
    override fun hashCode() = javaClass.hashCode()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val minSize = minOf(input.width, input.height)
        val radius = minSize / 2f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.DST_ATOP)
        }

        return createBitmap(minSize, minSize, input.config).applyCanvas {
            drawCircle(radius, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmap(input, radius - input.width / 2f, radius - input.height / 2f, paint)
        }
    }
}