package com.alexeymerov.radiostations.core.domain.usecase.profile

import android.net.Uri
import com.alexeymerov.radiostations.core.dto.UserDto
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProfileUsaCase {

    suspend fun saveUserData(userDto: UserDto)

    fun getUserData(): Flow<UserDto>

    suspend fun getAvatar(): File?

    suspend fun saveAvatar(uri: Uri, isFromCamera: Boolean)

    suspend fun deleteAvatar()

    fun getAvatarTempUri(): Uri
}