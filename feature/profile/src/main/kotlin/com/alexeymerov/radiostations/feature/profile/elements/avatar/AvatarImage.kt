package com.alexeymerov.radiostations.feature.profile.elements.avatar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.maxDialogHeight
import com.alexeymerov.radiostations.core.ui.extensions.maxDialogWidth
import com.alexeymerov.radiostations.feature.profile.ProfileTestTags

@Composable
internal fun AvatarImage(
    isLoaded: Boolean,
    avatarFile: String?,
    onLoadResult: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val colorFilter: ColorFilter? by remember(isLoaded) {
        derivedStateOf {
            if (isLoaded) null else ColorFilter.tint(colorScheme.onSecondary)
        }
    }

    AsyncImage(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(colorScheme.secondary)
            .clickable { if (isLoaded) onClick.invoke() }
            .testTag(ProfileTestTags.IMAGE),
        model = ImageRequest.Builder(LocalContext.current)
            .data(avatarFile)
            .allowHardware(false)
            .crossfade(500)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        error = rememberAsyncImagePainter(R.drawable.icon_person),
        colorFilter = colorFilter,
        onSuccess = { onLoadResult.invoke(true) },
        onError = { onLoadResult.invoke(false) }
    )
}

@Composable
internal fun EditRemoveIcons(isLoaded: Boolean, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.width(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedVisibility(
            visible = isLoaded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            IconButton(
                modifier = Modifier.testTag(ProfileTestTags.IMAGE_REMOVE),
                onClick = { onDelete.invoke() }
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.error,
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null
                )
            }
        }

        IconButton(
            modifier = Modifier.testTag(ProfileTestTags.IMAGE_EDIT),
            onClick = { onEdit.invoke() }
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.primary,
                imageVector = Icons.Outlined.Edit,
                contentDescription = null
            )
        }
    }
}

@Composable
internal fun BigPicture(avatarFile: String, onDismiss: () -> Unit) {
    val config = LocalConfiguration.current
    Dialog(
        onDismissRequest = { onDismiss.invoke() },
        content = {
            AsyncImage(
                modifier = Modifier
                    .run {
                        if (config.isLandscape()) {
                            height(config.maxDialogHeight())
                        } else {
                            width(config.maxDialogWidth())
                        }
                    }
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag(ProfileTestTags.IMAGE_BIG_PICTURE),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarFile)
                    .build(),
                contentDescription = null
            )
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AvatarBottomSheet(
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit
) {
    ModalBottomSheet(
        modifier = Modifier.testTag(ProfileTestTags.IMAGE_CHOOSE_SHEET),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { onDismiss.invoke() }
    ) {
        Row {
            val itemModifier = Modifier.weight(1f)

            BottomSheetItem(
                modifier = itemModifier,
                icon = Icons.Outlined.PhotoLibrary,
                text = stringResource(R.string.gallery),
                onAction = { onGallery.invoke() }
            )

            BottomSheetItem(
                modifier = itemModifier,
                icon = Icons.Outlined.CameraAlt,
                text = stringResource(R.string.camera),
                onAction = { onCamera.invoke() }
            )
        }
    }
}

@Composable
internal fun BottomSheetItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onAction: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onAction.invoke() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier
                .alpha(0.85f)
                .size(30.dp),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = text
        )
    }
}

@Composable
internal fun CameraPermissionRationale(
    onPermissionRequested: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(stringResource(R.string.camera_permission_needed_title)) },
        text = { Text(stringResource(R.string.camera_permission_needed_description)) },
        confirmButton = {
            TextButton(
                onClick = { onPermissionRequested.invoke() },
                content = { Text(stringResource(R.string.grant)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss.invoke() },
                content = { Text(stringResource(R.string.dismiss)) }
            )
        }
    )
}