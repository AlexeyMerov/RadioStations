package com.alexeymerov.radiostations.core.test

import android.graphics.Bitmap
import android.graphics.Color

fun createTestBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.RED)
    return bitmap
}