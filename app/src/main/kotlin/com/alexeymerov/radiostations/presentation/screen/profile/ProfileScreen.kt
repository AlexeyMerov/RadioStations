package com.alexeymerov.radiostations.presentation.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.common.ErrorView
import com.alexeymerov.radiostations.presentation.navigation.Screens
import com.alexeymerov.radiostations.presentation.navigation.TopBarState

@Composable
fun BaseProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    onNavigate: (String) -> Unit
) {
    if (isVisibleToUser) TopBarSetup(topBarBlock, onNavigate)

    ErrorView(errorText = "Coming soon", showImage = false)
}

@Composable
private fun TopBarSetup(
    topBarBlock: (TopBarState) -> Unit,
    onNavigate: (String) -> Unit
) {
    val title = stringResource(R.string.profile)
    val rightIcon = ImageVector.vectorResource(R.drawable.icon_settings)
    LaunchedEffect(Unit) {
        topBarBlock.invoke(
            TopBarState(
                title = title,
                rightIcon = rightIcon,
                rightIconAction = { onNavigate.invoke(Screens.Settings.route) }
            )
        )
    }
}