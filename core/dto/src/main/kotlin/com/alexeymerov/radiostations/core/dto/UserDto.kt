package com.alexeymerov.radiostations.core.dto

import java.io.File

data class UserDto(
    val avatarFile: File?,
    val name: TextFieldData,
    val email: TextFieldData,
    val countryCode: Int,
    val phoneNumber: TextFieldData,
    val isEverythingValid: Boolean = true
)

data class TextFieldData(
    val text: String,
    val errorTextResId: Int? = null
)