package com.alexeymerov.radiostations.core.filestore

import android.net.Uri
import java.io.File

interface AppFileStore {

    suspend fun getFileByName(name: String): File?

    suspend fun copyFromUriToFile(uri: Uri, fileName: String)

    suspend fun removeFileByUri(uri: Uri)

    fun getTempUri(prefix: String, suffix: String): Uri
}