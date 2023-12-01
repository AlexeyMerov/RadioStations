package com.alexeymerov.radiostations.domain.usecase.profile

import android.net.Uri
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.datastore.SettingsStore
import com.alexeymerov.radiostations.filestore.AppFileStore
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

class ProfileUsaCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore,
    private val fileStore: AppFileStore
) : ProfileUsaCase {

    override suspend fun getAvatar(): File? {
        val avatarFileName = settingsStore.getStringPrefsFlow(AVATAR_PREFIX, String.EMPTY).first()
        return fileStore.getFileByName(avatarFileName)
    }

    override suspend fun saveAvatar(uri: Uri, isFromCamera: Boolean) {
        val avatarFileName = AVATAR_PREFIX + System.currentTimeMillis() + AVATAR_EXT
        fileStore.copyFromUriToFile(uri, avatarFileName)
        settingsStore.setStringPrefs(AVATAR_PREFIX, avatarFileName)
        if (isFromCamera) fileStore.removeFileByUri(uri)
    }

    override fun getTempUri(): Uri {
        return fileStore.getTempUri(AVATAR_PREFIX, AVATAR_EXT)
    }

    companion object {
        const val AVATAR_PREFIX = "avatar_"
        const val AVATAR_EXT = ".jpg"
    }
}