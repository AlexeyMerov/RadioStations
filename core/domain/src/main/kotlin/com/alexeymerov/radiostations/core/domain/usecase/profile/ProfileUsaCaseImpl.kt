package com.alexeymerov.radiostations.core.domain.usecase.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.filestore.AppFileStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

class ProfileUsaCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore,
    private val fileStore: AppFileStore
) : ProfileUsaCase {

    private var lastTempUri: Uri? = null

    override suspend fun saveUserData(userDto: UserDto) {
        settingsStore.setStringPrefs(USER_NAME_KEY, userDto.name.text)
        settingsStore.setStringPrefs(USER_EMAIL_KEY, userDto.email.text)
        settingsStore.setIntPrefs(USER_COUNTRY_KEY, userDto.countryCode)
        settingsStore.setStringPrefs(USER_PHONE_KEY, userDto.phoneNumber.text)
    }

    override fun getUserData(): Flow<UserDto> {
        return combine(
            settingsStore.getStringPrefsFlow(AVATAR_PREFIX, String.EMPTY),
            settingsStore.getStringPrefsFlow(USER_NAME_KEY, "Jhon"),
            settingsStore.getStringPrefsFlow(USER_EMAIL_KEY, "jhon@jhon.com"),
            settingsStore.getIntPrefsFlow(USER_COUNTRY_KEY, 1),
            settingsStore.getStringPrefsFlow(USER_PHONE_KEY, "123456"),
        ) { fileName, name, email, countryCode, phone ->
            UserDto(
                avatarFile = fileStore.getFileByName(fileName),
                name = TextFieldData(name),
                email = TextFieldData(email),
                countryCode = countryCode,
                phoneNumber = TextFieldData(phone)
            )
        }
    }

    override suspend fun getAvatar(): File? {
        val avatarFileName = settingsStore.getStringPrefsFlow(AVATAR_PREFIX, String.EMPTY).first()
        return fileStore.getFileByName(avatarFileName)
    }

    override suspend fun saveAvatar(bitmap: Bitmap) {
        val avatarFileName = AVATAR_PREFIX + System.currentTimeMillis() + AVATAR_EXT
        fileStore.copyFromBitmapToFile(bitmap, avatarFileName)
        settingsStore.setStringPrefs(AVATAR_PREFIX, avatarFileName)

        resetTempFile()
    }

    private suspend fun resetTempFile() {
        lastTempUri?.let { fileStore.removeFileByUri(it) }
        lastTempUri = null
    }

    override suspend fun deleteAvatar() {
        val avatar = getAvatar() ?: return
        fileStore.removeFileByUri(avatar.toUri())
        settingsStore.setStringPrefs(AVATAR_PREFIX, String.EMPTY)
    }

    override fun getAvatarTempUri(): Uri {
        val tempUri = fileStore.getTempUri(AVATAR_PREFIX, AVATAR_EXT)
        lastTempUri = tempUri
        return tempUri
    }

    companion object {
        const val AVATAR_PREFIX = "avatar_"
        const val AVATAR_EXT = ".jpg"

        const val USER_NAME_KEY = "user_name_key"
        const val USER_EMAIL_KEY = "user_email_key"
        const val USER_COUNTRY_KEY = "user_country_key"
        const val USER_PHONE_KEY = "user_phone_key"

    }
}