package com.alexeymerov.radiostations.presentation.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.navigation.AppBarState
import com.alexeymerov.radiostations.presentation.navigation.Screens
import com.alexeymerov.radiostations.presentation.screen.common.ErrorView

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    appBarBlock: @Composable (AppBarState) -> Unit,
    onNavigate: (String) -> Unit
) {
    appBarBlock.invoke(
        AppBarState(
            title = stringResource(R.string.profile),
            rightIcon = ImageVector.vectorResource(R.drawable.icon_settings),
            rightIconAction = { onNavigate.invoke(Screens.Settings.route) }
        )
    )

    ErrorView(errorText = "Coming soon", showImage = false)
}