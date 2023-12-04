package com.alexeymerov.radiostations.core.filestore

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class AppFileStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppFileStore {

    override suspend fun getFileByName(name: String) = File(context.filesDir, name)

    override fun getTempUri(prefix: String, suffix: String): Uri {
        return FileProvider.getUriForFile(context, AUTHORITY, getTempFile(prefix, suffix))
    }

    private fun getTempFile(prefix: String, suffix: String): File = File.createTempFile(prefix, suffix, context.filesDir)

    override suspend fun removeFileByUri(uri: Uri) {
        Timber.d("removeFileByUri: $uri")
        uri.path?.let { File(it).delete() }
    }

    override suspend fun copyFromUriToFile(uri: Uri, fileName: String) {
        Timber.d("copyFromUriToFile: $uri")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        inputStream?.use { input ->
            Timber.d("inputStream: $input")
            outputStream.use { output ->
                Timber.d("outputStream: $output")
                val copied = input.copyTo(output)
                Timber.d("copied: $copied")
            }
        }
    }

    companion object {
        private const val AUTHORITY = "com.alexeymerov.radiostations.provider"
    }
}