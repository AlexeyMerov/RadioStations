package com.alexeymerov.radiostations.feature.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.feature.profile.elements.UserTextFields
import com.alexeymerov.radiostations.feature.profile.elements.avatar.AvatarImage
import com.alexeymerov.radiostations.feature.profile.elements.avatar.EditRemoveIcons

@Composable
internal fun ContentForLandscapeTabletScreen(
    userData: UserDto,
    inEdit: Boolean,
    onAction: (ProfileViewModel.ViewAction) -> Unit,
    onAvatarClick: () -> Unit,
    onAvatarEdit: () -> Unit,
    onCountryCode: () -> Unit
) {
    val config = LocalConfiguration.current
    var isLoaded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(horizontal = if (config.isLandscape()) 80.dp else 16.dp)
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarImage(
                isLoaded = isLoaded,
                avatarFile = userData.avatarFile,
                onLoadResult = { isLoaded = it },
                onClick = { onAvatarClick.invoke() }
            )

            AnimatedVisibility(visible = inEdit) {
                EditRemoveIcons(
                    isLoaded = isLoaded,
                    onDelete = { onAction.invoke(ProfileViewModel.ViewAction.DeleteImage) },
                    onEdit = onAvatarEdit
                )
            }
        }

        Box(Modifier.padding(start = 32.dp)) {
            UserTextFields(
                inEdit = inEdit,
                userData = userData,
                onAction = { onAction.invoke(it) },
                onCountryAction = { onCountryCode.invoke() }
            )
        }
    }
}