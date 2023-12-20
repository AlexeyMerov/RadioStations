package com.alexeymerov.radiostations.feature.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.isPortrait
import com.alexeymerov.radiostations.core.ui.extensions.isTablet
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.view.LoaderView
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewState
import com.alexeymerov.radiostations.feature.settings.connectivity.ConnectivitySettings
import com.alexeymerov.radiostations.feature.settings.theme.ThemeSettings

@Composable
fun BaseSettingsScreen(
    viewModel: SettingsViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit
) {
    if (isVisibleToUser) TopBarSetup(topBarBlock)

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val onViewAction: (ViewAction) -> Unit = { viewModel.setAction(it) }

    SettingsScreen(viewState, onViewAction)
}

@Composable
private fun TopBarSetup(topBarBlock: (TopBarState) -> Unit) {
    val title = stringResource(R.string.settings)
    LaunchedEffect(Unit) {
        topBarBlock.invoke(TopBarState(title = title, displayBackButton = true))
    }
}

@Composable
private fun SettingsScreen(
    viewState: ViewState,
    onAction: (ViewAction) -> Unit
) {
    when (viewState) {
        ViewState.Loading -> LoaderView()
        is ViewState.Loaded -> MainContent(viewState.themeState, viewState.connectionStatus, onAction)
    }
}

@Composable
private fun MainContent(
    themeState: ThemeState,
    connectionStatus: ConnectionStatus,
    onAction: (ViewAction) -> Unit
) {
    val config = LocalConfiguration.current
    var modifier = Modifier.fillMaxSize()
    modifier = if (config.isTablet()) {
        modifier.padding(
            vertical = 16.dp,
            horizontal = if (config.isPortrait()) 160.dp else 320.dp
        )
    } else {
        modifier.padding(16.dp)
    }

    // not sure about tablet/wide layout therefore left default paddings
    Column(modifier) {
        var currentTab by rememberSaveable { mutableStateOf(SettingTab.USER) }
        TabRow(
            currentTab = currentTab,
            onTabClick = { currentTab = it }
        )

        AnimatedContent(
            targetState = currentTab,
            label = "",
            transitionSpec = {
                val start = AnimatedContentTransitionScope.SlideDirection.Start
                val end = AnimatedContentTransitionScope.SlideDirection.End
                val direction = if (targetState.index > initialState.index) start else end

                slideIntoContainer(direction)
                    .togetherWith(slideOutOfContainer(direction))
            }
        ) { tab ->
            when (tab) {
                SettingTab.USER -> ThemeSettings(themeState, onAction)
                SettingTab.DEV -> ConnectivitySettings(connectionStatus, onAction)
            }
        }
    }
}