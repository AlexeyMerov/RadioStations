package com.alexeymerov.radiostations.presentation.screen.profile

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.presentation.common.rememberGalleyPicker
import com.alexeymerov.radiostations.presentation.common.rememberTakePicture
import com.alexeymerov.radiostations.presentation.common.rememberTextPainter
import com.alexeymerov.radiostations.presentation.navigation.RightIconItem
import com.alexeymerov.radiostations.presentation.navigation.Screens
import com.alexeymerov.radiostations.presentation.navigation.TopBarIcon
import com.alexeymerov.radiostations.presentation.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewAction
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import timber.log.Timber
import java.io.File


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BaseProfileScreen(
    viewModel: ProfileViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    onNavigate: (String) -> Unit
) {
    if (isVisibleToUser) TopBarSetup(topBarBlock, onNavigate)

    val context = LocalContext.current

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var showRationaleDialog by remember { mutableStateOf(false) }

    val singlePhotoPickerLauncher = rememberGalleyPicker { viewModel.setAction(ViewAction.SaveGalleryImage(it)) }
    val cameraLauncher = rememberTakePicture { viewModel.setAction(ViewAction.SaveCameraImage) }

    val avatarFile by viewModel.avatar

    MainContent(
        avatarFile = avatarFile,
        onEdit = { showBottomSheet = true },
        onDelete = { viewModel.setAction(ViewAction.DeleteImage) }
    )

    BottomSheet(
        showBottomSheet = showBottomSheet,
        onDismiss = { showBottomSheet = false },
        onGallery = {
            showBottomSheet = false
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onCamera = {
            showBottomSheet = false

            val status = cameraPermissionState.status
            Timber.d("status $status")
            when {
                status.shouldShowRationale -> showRationaleDialog = true
                status is PermissionStatus.Denied -> cameraPermissionState.launchPermissionRequest()
                status is PermissionStatus.Granted -> cameraLauncher.launch(viewModel.tempUri)
            }
        }
    )

    if (showRationaleDialog) {
        CameraPermissionRationale(
            onPermissionRequested = {
                Timber.d("onPermissionRequested")
                showRationaleDialog = false
                val intent = Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            },
            onDismiss = { showRationaleDialog = false }
        )
    }
}

@Composable
fun CameraPermissionRationale(
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss.invoke() },
            sheetState = sheetState
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
}

@Composable
fun BottomSheetItem(
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
            contentDescription = String.EMPTY
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = text
        )
    }
}


@Composable
private fun MainContent(
    avatarFile: File?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var needShowBigPicture by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            IconButton(onClick = { onDelete.invoke() }) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.error,
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null
                )
            }

            val placeholder = rememberTextPainter(
                containerColor = MaterialTheme.colorScheme.primary,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                text = stringResource(R.string.avatar)
            )

            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable { if (avatarFile != null) needShowBigPicture = true },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarFile)
                    .crossfade(500)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = placeholder
            )

            IconButton(onClick = { onEdit.invoke() }) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
        }

        if (needShowBigPicture) {
            Dialog(
                onDismissRequest = { needShowBigPicture = !needShowBigPicture },
                content = {
                    AsyncImage(
                        modifier = Modifier
                            .width(300.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatarFile)
                            .build(),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun TopBarSetup(
    topBarBlock: (TopBarState) -> Unit,
    onNavigate: (String) -> Unit
) {
    val title = stringResource(R.string.profile)
    LaunchedEffect(Unit) {
        topBarBlock.invoke(
            TopBarState(
                title = title,
                rightIcon = RightIconItem(TopBarIcon.SETTINGS).apply {
                    action = { onNavigate.invoke(Screens.Settings.route) }
                }
            )
        )
    }
}