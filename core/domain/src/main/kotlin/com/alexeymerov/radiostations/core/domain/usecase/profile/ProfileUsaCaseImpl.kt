package com.alexeymerov.radiostations.core.domain.usecase.profile

import android.net.Uri
import androidx.core.net.toUri
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.filestore.AppFileStore
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

    override suspend fun deleteAvatar() {
        val avatar = getAvatar() ?: return
        fileStore.removeFileByUri(avatar.toUri())
        settingsStore.setStringPrefs(AVATAR_PREFIX, String.EMPTY)
    }

    override fun getTempUri(): Uri {
        return fileStore.getTempUri(AVATAR_PREFIX, AVATAR_EXT)
    }

    companion object {
        const val AVATAR_PREFIX = "avatar_"
        const val AVATAR_EXT = ".jpg"
    }
}