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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.LocalTopBarScroll
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isTablet
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.remembers.rememberGalleyPicker
import com.alexeymerov.radiostations.core.ui.remembers.rememberTakePicture
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel.ViewAction
import com.alexeymerov.radiostations.feature.profile.elements.AvatarBottomSheet
import com.alexeymerov.radiostations.feature.profile.elements.AvatarImage
import com.alexeymerov.radiostations.feature.profile.elements.BigPicture
import com.alexeymerov.radiostations.feature.profile.elements.CameraPermissionRationale
import com.alexeymerov.radiostations.feature.profile.elements.CountriesBottomSheet
import com.alexeymerov.radiostations.feature.profile.elements.EditRemoveIcons
import com.alexeymerov.radiostations.feature.profile.elements.UserTextFields
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import timber.log.Timber


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BaseProfileScreen(
    viewModel: ProfileViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    onNavigate: (String) -> Unit
) {
    if (isVisibleToUser) TopBarSetup(topBarBlock)

    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var showCameraRationaleDialog by remember { mutableStateOf(false) }
    val singlePhotoPickerLauncher = rememberGalleyPicker { viewModel.setAction(ViewAction.SaveGalleryImage(it)) }
    val cameraLauncher = rememberTakePicture { viewModel.setAction(ViewAction.SaveCameraImage) }

    var showCountriesBottomSheet by rememberSaveable { mutableStateOf(false) }
    if (showCountriesBottomSheet) {
        val countries = viewModel.countryCodes.collectAsLazyPagingItems()
        CountriesBottomSheet(
            countries = countries,
            onSearch = { viewModel.setAction(ViewAction.SearchCountry(it)) },
            onSelect = {
                viewModel.setAction(ViewAction.NewCountry(it))
                showCountriesBottomSheet = false
            },
            onDismiss = { showCountriesBottomSheet = false }
        )
    }

    var showChangeAvatarBottomSheet by rememberSaveable { mutableStateOf(false) }
    if (showChangeAvatarBottomSheet) {
        AvatarBottomSheet(
            onDismiss = { showChangeAvatarBottomSheet = false },
            onGallery = {
                showChangeAvatarBottomSheet = false
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onCamera = {
                showChangeAvatarBottomSheet = false

                val status = cameraPermissionState.status
                Timber.d("status $status")
                when {
                    status.shouldShowRationale -> showCameraRationaleDialog = true
                    status is PermissionStatus.Denied -> cameraPermissionState.launchPermissionRequest()
                    status is PermissionStatus.Granted -> cameraLauncher.launch(viewModel.tempUri)
                }
            }
        )
    }

    if (showCameraRationaleDialog) {
        CameraPermissionRationale(
            onPermissionRequested = {
                Timber.d("onPermissionRequested")
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

    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val data by viewModel.userData.collectAsStateWithLifecycle()
    data?.let { userDto ->
        MainContent(
            inEdit = state is ProfileViewModel.ViewState.InEdit,
            userData = userDto,
            onNavigate = onNavigate,
            onAvatarEdit = { showChangeAvatarBottomSheet = true },
            onAction = { viewModel.setAction(it) },
            onCountryCode = { showCountriesBottomSheet = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    inEdit: Boolean,
    userData: UserDto,
    onNavigate: (String) -> Unit,
    onAvatarEdit: () -> Unit,
    onAction: (ViewAction) -> Unit,
    onCountryCode: () -> Unit,
) {
    val config = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    var needShowBigPicture by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(LocalTopBarScroll.current.nestedScrollConnection)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        if (config.isLandscape() && config.isTablet()) {
            ContentForTabletScreen(
                userData = userData,
                inEdit = inEdit,
                onAction = onAction,
                onAvatarClick = { needShowBigPicture = true },
                onAvatarEdit = onAvatarEdit,
                onCountryCode = onCountryCode
            )
        } else {
            ContentForPhoneScreen(
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
            label = ""
        ) { isEditMode ->
            AnimatedVisibility(visible = userData.isEverythingValid) {
                IconButton(
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
private fun ContentForPhoneScreen(
    userData: UserDto,
    inEdit: Boolean,
    onAction: (ViewAction) -> Unit,
    onAvatarClick: () -> Unit,
    onAvatarEdit: () -> Unit,
    onCountryCode: () -> Unit
) {
    val config = LocalConfiguration.current
    var isLoaded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = if (config.isLandscape()) 80.dp else 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
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
                    onDelete = { onAction.invoke(ViewAction.DeleteImage) },
                    onEdit = onAvatarEdit
                )
            }
        }

        Box(Modifier.padding(vertical = 16.dp)) {
            UserTextFields(
                inEdit = inEdit,
                userData = userData,
                onAction = { onAction.invoke(it) },
                onCountryAction = { onCountryCode.invoke() }
            )
        }
    }
}

@Composable
private fun ContentForTabletScreen(
    userData: UserDto,
    inEdit: Boolean,
    onAction: (ViewAction) -> Unit,
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
                    onDelete = { onAction.invoke(ViewAction.DeleteImage) },
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

@Composable
private fun TopBarSetup(topBarBlock: (TopBarState) -> Unit) {
    val title = stringResource(R.string.profile)
    LaunchedEffect(Unit) {
        topBarBlock.invoke(
            TopBarState(title = title)
        )
    }
}