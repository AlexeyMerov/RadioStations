package com.alexeymerov.radiostations.core.domain.usecase.profile

import android.graphics.Bitmap
import android.net.Uri
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeProfileUsaCase : ProfileUsaCase {

    var validUserDto = UserDto(
        avatarFile = null,
        name = TextFieldData("John"),
        email = TextFieldData("john@john.com"),
        countryCode = 3,
        phoneNumber = TextFieldData("2345"),
        isEverythingValid = true

    )

    val userDataFlow = MutableStateFlow(validUserDto)

    override suspend fun saveUserData(userDto: UserDto) {
        validUserDto = userDto
        userDataFlow.emit(validUserDto)
    }

    override fun getUserData(): Flow<UserDto> {
        return userDataFlow
    }

    override suspend fun getAvatar(): String? = validUserDto.avatarFile

    override suspend fun saveAvatar(bitmap: Bitmap) {}

    override suspend fun deleteAvatar() {
        validUserDto = validUserDto.copy(avatarFile = null)
        userDataFlow.emit(validUserDto)
    }

    override fun getAvatarTempUri(): Uri = Uri.parse("tst")
}