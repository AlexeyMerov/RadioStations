package com.alexeymerov.radiostations.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
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
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags.PAGER
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel.ViewState
import com.alexeymerov.radiostations.feature.settings.connectivity.ConnectivitySettings
import com.alexeymerov.radiostations.feature.settings.language.LanguageSettings
import com.alexeymerov.radiostations.feature.settings.theme.ThemeSettings
import kotlinx.coroutines.launch


@Composable
fun BaseSettingsScreen(
    viewModel: SettingsViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit
) {
    if (isVisibleToUser) TopBarSetup(topBarBlock)

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val onViewAction: (ViewAction) -> Unit = { viewModel.setAction(it) }

    SettingsScreen(
        viewState = viewState,
        onAction = onViewAction
    )
}

@Composable
private fun TopBarSetup(topBarBlock: (TopBarState) -> Unit) {
    val title = stringResource(R.string.settings)
    LaunchedEffect(Unit) {
        topBarBlock.invoke(TopBarState(title = title, displayBackButton = true))
    }
}

@Composable
internal fun SettingsScreen(
    viewState: ViewState,
    onAction: (ViewAction) -> Unit
) {
    val config = LocalConfiguration.current
    when (viewState) {
        ViewState.Loading -> LoaderView()
        is ViewState.Loaded -> {
            if (config.isTablet()) {
                MainContentTablet(
                    themeState = viewState.themeState,
                    connectionStatus = viewState.connectionStatus,
                    onAction = onAction
                )
            } else {
                MainContent(
                    themeState = viewState.themeState,
                    connectionStatus = viewState.connectionStatus,
                    onAction = onAction
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    themeState: ThemeState,
    connectionStatus: ConnectionStatus,
    onAction: (ViewAction) -> Unit
) {
    val config = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()

    var currentTab by rememberSaveable { mutableStateOf(SettingTab.USER) }
    val pagerState = rememberPagerState { SettingTab.entries.size }

    LaunchedEffect(pagerState.currentPage) {
        currentTab = SettingTab.entries[pagerState.currentPage]
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SettingsTestTags.CONTENT),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SettingsTabRow(
            currentTab = currentTab,
            onTabClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(it.index)
                }
            }
        )

        HorizontalPager(
            modifier = Modifier.testTag(PAGER),
            state = pagerState
        ) { pageIndex ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        vertical = 16.dp,
                        horizontal = when {
                            config.isTablet() && config.isPortrait() -> 160.dp
                            config.isTablet() -> 320.dp
                            else -> 16.dp
                        }
                    )
            ) {
                when (pageIndex) {
                    SettingTab.USER.index -> UserSettings(themeState, onAction)
                    SettingTab.DEV.index -> DevSettings(connectionStatus, onAction)
                }
            }
        }
    }
}

@Composable
private fun MainContentTablet(
    themeState: ThemeState,
    connectionStatus: ConnectionStatus,
    onAction: (ViewAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SettingsTestTags.CONTENT_TABLET),
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

            HorizontalDivider(thickness = 0.5.dp)

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

            HorizontalDivider(thickness = 0.5.dp)

            ConnectivitySettings(
                modifier = contentModifier,
                connectionStatus = connectionStatus,
                onAction = onAction
            )
        }
    }
}

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
