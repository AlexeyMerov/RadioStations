package com.alexeymerov.radiostations.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ColorTheme
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.DarkLightMode
import com.alexeymerov.radiostations.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.common.BasicText
import com.alexeymerov.radiostations.presentation.common.LoaderView
import com.alexeymerov.radiostations.presentation.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewState
import com.alexeymerov.radiostations.presentation.theme.blue.BlueLightColors
import com.alexeymerov.radiostations.presentation.theme.green.GreenLightColors
import com.alexeymerov.radiostations.presentation.theme.orange.OrangeLightColors

@Composable
fun BaseSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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
    // more settings later

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ThemeSettings(themeState, onAction)
        ConnectivitySettings(connectionStatus, onAction)
    }
}

@Composable
private fun ConnectivitySettings(
    connectionStatus: ConnectionStatus,
    onAction: (ViewAction) -> Unit
) {
    val isOnline = connectionStatus == ConnectionStatus.ONLINE
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(text = "Request new items")

        Switch(
            checked = isOnline,
            onCheckedChange = {
                onAction.invoke(
                    ViewAction.ChangeConnection(
                        if (isOnline) ConnectionStatus.OFFLINE else ConnectionStatus.ONLINE
                    )
                )
            })
    }
}

@Composable
private fun ThemeSettings(
    themeState: ThemeState,
    onAction: (ViewAction) -> Unit
) {
    var needShowThemeDialog by rememberSaveable { mutableStateOf(false) }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        BlueLightColors.primary,
                        GreenLightColors.primary,
                        OrangeLightColors.primary,
                    )
                ),
                shape = ButtonDefaults.shape
            )
            .height(ButtonDefaults.MinHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        onClick = { needShowThemeDialog = !needShowThemeDialog }) {
        Text(text = stringResource(R.string.theme_settings))
    }

    if (needShowThemeDialog) {
        ThemeDialog(
            themeState = themeState,
            onDismiss = { needShowThemeDialog = !needShowThemeDialog },
            onViewAction = onAction
        )
    }
}

@Composable
private fun ThemeDialog(
    themeState: ThemeState,
    onDismiss: () -> Unit,
    onViewAction: (ViewAction) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                modifier = Modifier.clickable(onClick = onDismiss),
                text = stringResource(R.string.done)
            )
        },
        title = { Text(text = stringResource(R.string.theme_settings)) },
        text = {
            Column {
                DarkThemeOptions(themeState.darkLightMode, onAction = onViewAction)
                DynamicColorOptions(themeState.useDynamicColor, onAction = onViewAction)

//              AnimatedVisibility - feels like frame drop, smth with AlertDialog, maybe add later
                if (!themeState.useDynamicColor) {
                    ColorOptions(themeState.colorTheme, onAction = onViewAction)
                }
            }
        }
    )
}

@Composable
private fun DarkThemeOptions(
    darkLightMode: DarkLightMode,
    onAction: (ViewAction) -> Unit
) {
    BasicText(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(R.string.dark_mode), textStyle = MaterialTheme.typography.titleMedium
    )

    BasicRadioButton(
        isSelected = darkLightMode == DarkLightMode.System,
        text = stringResource(R.string.system),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkLightMode.System)) }
    )

    BasicRadioButton(
        isSelected = darkLightMode == DarkLightMode.Light,
        text = stringResource(R.string.light),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkLightMode.Light)) }
    )

    BasicRadioButton(
        isSelected = darkLightMode == DarkLightMode.Dark,
        text = stringResource(R.string.dark),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkLightMode.Dark)) }
    )

    BasicRadioButton(
        isSelected = darkLightMode == DarkLightMode.Night,
        text = stringResource(R.string.night),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkLightMode.Night)) }
    )
}

@Composable
private fun DynamicColorOptions(
    useDynamicColor: Boolean,
    onAction: (ViewAction) -> Unit
) {
    //val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S check is show

    BasicText(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        text = stringResource(R.string.dynamic_color),
        textStyle = MaterialTheme.typography.titleMedium
    )

    BasicRadioButton(
        isSelected = useDynamicColor,
        text = stringResource(R.string.yes),
        action = { onAction.invoke(ViewAction.ChangeDynamicColor(true)) }
    )

    BasicRadioButton(
        isSelected = !useDynamicColor,
        text = stringResource(R.string.no),
        action = { onAction.invoke(ViewAction.ChangeDynamicColor(false)) }
    )
}

@Composable
private fun ColorOptions(
    colorTheme: ColorTheme,
    onAction: (ViewAction) -> Unit
) {
    Text(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        text = stringResource(R.string.color),
        style = MaterialTheme.typography.titleMedium
    )

    BasicRadioButton(
        isSelected = colorTheme == ColorTheme.DefaultBlue,
        text = stringResource(R.string.default_blue),
        action = { onAction.invoke(ViewAction.ChangeColorScheme(ColorTheme.DefaultBlue)) }
    )

    BasicRadioButton(
        isSelected = colorTheme == ColorTheme.Green,
        text = stringResource(R.string.green),
        action = { onAction.invoke(ViewAction.ChangeColorScheme(ColorTheme.Green)) }
    )

    BasicRadioButton(
        isSelected = colorTheme == ColorTheme.Orange,
        text = stringResource(R.string.orange),
        action = { onAction.invoke(ViewAction.ChangeColorScheme(ColorTheme.Orange)) }
    )
}

@Composable
private fun BasicRadioButton(
    isSelected: Boolean,
    text: String,
    action: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = action,
            )
            .padding(start = 24.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            modifier = Modifier.padding(top = 2.dp), //by default not aligned with text center
            selected = isSelected,
            onClick = null
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }
}

@Composable
@Preview
private fun PreviewRadioButton() {
    BasicRadioButton(true, "Some text", {})
}


