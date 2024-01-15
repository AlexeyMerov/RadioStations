package com.alexeymerov.radiostations.core.filestore

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface AppFileStore {

    suspend fun getFileByName(name: String): File?

    suspend fun copyFromBitmapToFile(bitmap: Bitmap, fileName: String)

    suspend fun removeFileByUri(uri: Uri)

    fun getTempUri(prefix: String, suffix: String): Uri
}