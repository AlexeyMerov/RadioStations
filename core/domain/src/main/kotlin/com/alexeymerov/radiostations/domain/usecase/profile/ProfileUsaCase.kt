package com.alexeymerov.radiostations.domain.usecase.profile

import android.net.Uri
import java.io.File

interface ProfileUsaCase {

    suspend fun getAvatar(): File?

    suspend fun saveAvatar(uri: Uri, isFromCamera: Boolean)

    fun getTempUri(): Uri
}