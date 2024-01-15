package com.alexeymerov.radiostations.feature.profile.helpers

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun ContentResolver.loadBitmap(uri: Uri): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        openInputStream(uri).use {
            BitmapFactory.decodeStream(it)?.asImageBitmap()
        }
    }
}

internal suspend fun ImageBitmap.rotate(degrees: Float): ImageBitmap {
    return this.asAndroidBitmap().rotate(degrees).asImageBitmap()
}

internal suspend fun Bitmap.rotate(degrees: Float): Bitmap {
    return withContext(Dispatchers.IO) {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return@withContext Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
    }
}