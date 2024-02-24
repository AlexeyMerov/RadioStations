package com.alexeymerov.radiostations.core.filestore

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.alexeymerov.radiostations.core.filestore.AppFileStore.FileSuffix
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class AppFileStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppFileStore {

    override suspend fun getFileByName(name: String): File? {
        val file = File(context.filesDir, name)
        if (file.isDirectory) return null
        return file
    }

    override fun getTempUri(prefix: String, suffix: FileSuffix): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", getTempFile(prefix, suffix.value))
    }

    private fun getTempFile(prefix: String, suffix: String): File = File.createTempFile(prefix, suffix, context.filesDir)

    override suspend fun removeFileByUri(uri: Uri) {
        Timber.d("removeFileByUri: $uri")
        uri.path?.let { File(it).delete() }
    }

    override suspend fun copyFromBitmapToFile(bitmap: Bitmap, fileName: String) {
        Timber.d("copyFromBitmapToFile: $fileName")

        val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        outputStream.use { output ->
            Timber.d("copyFromBitmapToFile - outputStream: $output")
            val saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            output.flush()
            Timber.d("copyFromBitmapToFile - saved: $saved")
        }
    }
}