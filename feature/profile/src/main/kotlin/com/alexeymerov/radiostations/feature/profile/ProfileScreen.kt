package com.alexeymerov.radiostations.feature.profile

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.LocalTopbar
import com.alexeymerov.radiostations.core.ui.common.TopBarState
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isTablet
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.remembers.rememberGalleyPicker
import com.alexeymerov.radiostations.core.ui.remembers.rememberTakePicture
import com.alexeymerov.radiostations.core.ui.view.LoaderView
import com.alexeymerov.radiostations.feature.profile.ProfileTestTags.EDIT_SAVE_ICON
import com.alexeymerov.radiostations.feature.profile.ProfileTestTags.MAIN_CONTENT
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel.ViewAction
import com.alexeymerov.radiostations.feature.profile.elements.CountriesBottomSheet
import com.alexeymerov.radiostations.feature.profile.elements.avatar.AvatarBottomSheet
import com.alexeymerov.radiostations.feature.profile.elements.avatar.BigPicture
import com.alexeymerov.radiostations.feature.profile.elements.avatar.CameraPermissionRationale
import com.alexeymerov.radiostations.feature.profile.elements.avatar.Cropper
import com.alexeymerov.radiostations.feature.profile.helpers.loadBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun BaseProfileScreen(
    viewModel: ProfileViewModel,
    isVisibleToUser: Boolean,
    onNavigate: (String) -> Unit
) {
    if (isVisibleToUser) TopBarSetup()

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ProfileScreen(
        viewState = viewState,
        onAction = { viewModel.setAction(it) },
        onNavigate = onNavigate
    )
}

@Composable
internal fun ProfileScreen(
    viewState: ProfileViewModel.ViewState,
    onAction: (ViewAction) -> Unit,
    onNavigate: (String) -> Unit
) {
    var showChangeAvatarBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showCountriesBottomSheet by rememberSaveable { mutableStateOf(false) }

    if (viewState is ProfileViewModel.ViewState.Loading) {
        LoaderView()
    } else {
        if (viewState is ProfileViewModel.ViewState.InEdit) {
            ProfileScreenEditMode(
                editState = viewState,
                onAction = onAction,
                showChangeAvatarBottomSheet = showChangeAvatarBottomSheet,
                showCountriesBottomSheet = showCountriesBottomSheet,
                onDismissDialogs = {
                    showChangeAvatarBottomSheet = false
                    showCountriesBottomSheet = false
                }
            )
        }

        MainContent(
            inEdit = viewState is ProfileViewModel.ViewState.InEdit,
            userData = viewState.userData,
            onNavigate = onNavigate,
            onAction = { onAction.invoke(it) },
            onAvatarEdit = { showChangeAvatarBottomSheet = true },
            onCountryCode = { showCountriesBottomSheet = true }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun ProfileScreenEditMode(
    editState: ProfileViewModel.ViewState.InEdit,
    onAction: (ViewAction) -> Unit,
    showChangeAvatarBottomSheet: Boolean,
    showCountriesBottomSheet: Boolean,
    onDismissDialogs: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    /*
    * Crop any selected image block
    * */
    var imageToCrop by remember { mutableStateOf<ImageBitmap?>(null) }
    imageToCrop?.let { imageBitmap ->
        Cropper(
            imageToCrop = imageBitmap,
            onUpdateImage = { imageToCrop = it },
            onClose = {
                if (it != null) onAction.invoke(ViewAction.SaveCroppedImage(it))
                imageToCrop = null
            }
        )
    }

    /*
    * Take images from gallery or camera bottom sheet
    * */
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var showCameraRationaleDialog by remember { mutableStateOf(false) }
    val singlePhotoPickerLauncher = rememberGalleyPicker {
        coroutineScope.launch {
            imageToCrop = context.contentResolver.loadBitmap(it)
        }
    }
    val cameraLauncher = rememberTakePicture {
        coroutineScope.launch {
            imageToCrop = context.contentResolver.loadBitmap(editState.tempUri)
        }
    }

    if (showChangeAvatarBottomSheet) {
        AvatarBottomSheet(
            onDismiss = { onDismissDialogs.invoke() },
            onGallery = {
                onDismissDialogs.invoke()
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onCamera = {
                onDismissDialogs.invoke()

                val status = cameraPermissionState.status
                Timber.d("ProfileScreenEditMode cameraPermissionState $status")
                when {
                    status.shouldShowRationale -> showCameraRationaleDialog = true
                    status is PermissionStatus.Denied -> cameraPermissionState.launchPermissionRequest()
                    status is PermissionStatus.Granted -> cameraLauncher.launch(editState.tempUri)
                }
            }
        )
    }

    if (showCameraRationaleDialog) {
        CameraPermissionRationale(
            onPermissionRequested = {
                Timber.d("CameraPermissionRationale - onPermissionRequested")
                showCameraRationaleDialog = false
                val intent = Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            },
            onDismiss = { showCameraRationaleDialog = false }
        )
    }

    /*
    * Countries bottom sheet block
    * */
    if (showCountriesBottomSheet) {
        val countries = editState.countryCodes.collectAsLazyPagingItems()
        CountriesBottomSheet(
            countries = countries,
            onSearch = { onAction.invoke(ViewAction.SearchCountry(it)) },
            onSelect = {
                onAction.invoke(ViewAction.NewCountry(it))
                onDismissDialogs.invoke()
            },
            onDismiss = { onDismissDialogs.invoke() }
        )
    }
}

@Composable
private fun MainContent(
    inEdit: Boolean,
    userData: UserDto,
    onNavigate: (String) -> Unit,
    onAction: (ViewAction) -> Unit,
    onAvatarEdit: () -> Unit,
    onCountryCode: () -> Unit,
) {
    val config = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    var needShowBigPicture by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            }
            .testTag(MAIN_CONTENT),
        contentAlignment = Alignment.TopCenter,
    ) {
        if (config.isLandscape() && config.isTablet()) {
            ContentForLandscapeTabletScreen(
                userData = userData,
                inEdit = inEdit,
                onAction = onAction,
                onAvatarClick = { needShowBigPicture = true },
                onAvatarEdit = onAvatarEdit,
                onCountryCode = onCountryCode
            )
        } else {
            ContentForRegularScreen(
                userData = userData,
                inEdit = inEdit,
                onAction = onAction,
                onAvatarClick = { needShowBigPicture = true },
                onAvatarEdit = onAvatarEdit,
                onCountryCode = onCountryCode
            )
        }

        AnimatedContent(
            modifier = Modifier.align(Alignment.TopStart),
            targetState = inEdit,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = "inEdit"
        ) { isEditMode ->
            AnimatedVisibility(visible = userData.isEverythingValid) {
                IconButton(
                    modifier = Modifier.testTag(EDIT_SAVE_ICON),
                    onClick = {
                        val action = if (isEditMode) ViewAction.SaveEditsAndExitMode else ViewAction.EnterEditMode
                        onAction.invoke(action)
                    }
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Rounded.Done else Icons.Outlined.Edit,
                        contentDescription = null
                    )
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            visible = !inEdit
        ) {
            IconButton(onClick = { onNavigate.invoke(Screens.Settings.route) }) {
                Icon(
                    painter = rememberAsyncImagePainter(R.drawable.icon_settings),
                    contentDescription = null
                )
            }
        }

    }
    if (needShowBigPicture) {
        userData.avatarFile?.let { file ->
            BigPicture(
                avatarFile = file,
                onDismiss = { needShowBigPicture = !needShowBigPicture }
            )
        }
    }
}

@Composable
private fun TopBarSetup() {
    val context = LocalContext.current
    val topBar = LocalTopbar.current
    LaunchedEffect(Unit) {
        topBar.invoke(
            TopBarState(title = context.getString(R.string.profile))
        )
    }
}