package com.alexeymerov.radiostations.feature.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.core.ui.view.LoaderView
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewState
import com.alexeymerov.radiostations.feature.settings.connectivity.ConnectivitySettings
import com.alexeymerov.radiostations.feature.settings.language.LanguageSettings
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
    val config = LocalConfiguration.current
    when (viewState) {
        ViewState.Loading -> LoaderView()
        is ViewState.Loaded -> {
            if (config.isTablet()) MainContentTablet(viewState.themeState, viewState.connectionStatus, onAction)
            else MainContent(viewState.themeState, viewState.connectionStatus, onAction)
        }
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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var currentTab by rememberSaveable { mutableStateOf(SettingTab.USER) }
        SettingsTabRow(
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
            Column(modifier) {
                when (tab) {
                    SettingTab.USER -> UserSettings(themeState, onAction)
                    SettingTab.DEV -> DevSettings(connectionStatus, onAction)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserSettings(
    themeState: ThemeState,
    onAction: (ViewAction) -> Unit
) {
    ThemeSettings(
        modifier = Modifier.fillMaxWidth(),
        themeState = themeState,
        onAction = onAction
    )

    LanguageSettings(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}

@Composable
private fun DevSettings(
    connectionStatus: ConnectionStatus,
    onAction: (ViewAction) -> Unit
) {
    ConnectivitySettings(
        modifier = Modifier.fillMaxWidth(),
        connectionStatus = connectionStatus,
        onAction = onAction
    )
}

@Composable
private fun MainContentTablet(
    themeState: ThemeState,
    connectionStatus: ConnectionStatus,
    onAction: (ViewAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val columnModifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        val contentModifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

        Column(
            modifier = columnModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                modifier = Modifier.padding(8.dp),
                text = stringResource(SettingTab.USER.stringId)
            )
            Box(
                Modifier
                    .padding(horizontal = 64.dp) // static but consider calculate width of text
                    .height(4.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
            )

            Divider(thickness = 0.5.dp)

            ThemeSettings(
                modifier = contentModifier,
                themeState = themeState,
                onAction = onAction
            )

            LanguageSettings(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp)
            )
        }

        Column(
            modifier = columnModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                modifier = Modifier.padding(8.dp),
                text = stringResource(SettingTab.DEV.stringId)
            )

            Box(
                Modifier
                    .padding(horizontal = 64.dp) // static but consider calculate width of text
                    .height(4.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
            )

            Divider(thickness = 0.5.dp)

            ConnectivitySettings(
                modifier = contentModifier,
                connectionStatus = connectionStatus,
                onAction = onAction
            )
        }
    }
}