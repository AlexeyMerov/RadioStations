package com.alexeymerov.radiostations.presentation.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.common.view.ErrorView
import com.alexeymerov.radiostations.presentation.navigation.RightIconItem
import com.alexeymerov.radiostations.presentation.navigation.Screens
import com.alexeymerov.radiostations.presentation.navigation.TopBarIcon
import com.alexeymerov.radiostations.presentation.navigation.TopBarState

@Composable
fun BaseProfileScreen(
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