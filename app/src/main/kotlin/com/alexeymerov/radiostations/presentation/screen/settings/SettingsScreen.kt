package com.alexeymerov.radiostations.presentation.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ColorState
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.DarkModeState
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.screen.common.BasicText
import com.alexeymerov.radiostations.presentation.screen.common.LoaderView
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.settings.SettingsViewModel.ViewState

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val viewState by settingsViewModel.viewState.collectAsStateWithLifecycle()
    val onViewAction: (ViewAction) -> Unit = { settingsViewModel.setAction(it) }

    when (val state = viewState) {
        ViewState.Loading -> LoaderView()
        is ViewState.Loaded -> {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                DarkThemeOptions(state.themeState, onAction = onViewAction)
                DynamicColorOptions(state.themeState, onAction = onViewAction)

                AnimatedVisibility(visible = !state.themeState.useDynamicColor) {
                    ColorOptions(state.themeState, onAction = onViewAction)
                }
            }
        }
    }
}

@Composable
private fun DarkThemeOptions(
    themeState: ThemeState,
    onAction: (ViewAction) -> Unit
) {
    BasicText(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(R.string.dark_mode), textStyle = MaterialTheme.typography.titleMedium
    )

    BasicRadioButton(
        isSelected = themeState.darkMode == DarkModeState.System,
        text = stringResource(R.string.system),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkModeState.System)) }
    )

    BasicRadioButton(
        isSelected = themeState.darkMode == DarkModeState.Dark,
        text = stringResource(R.string.dark),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkModeState.Dark)) }
    )

    BasicRadioButton(
        isSelected = themeState.darkMode == DarkModeState.Light,
        text = stringResource(R.string.light),
        action = { onAction.invoke(ViewAction.ChangeDarkMode(DarkModeState.Light)) }
    )
}

@Composable
private fun DynamicColorOptions(
    themeState: ThemeState,
    onAction: (ViewAction) -> Unit
) {
    //val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S check is show

    BasicText(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        text = stringResource(R.string.dynamic_color),
        textStyle = MaterialTheme.typography.titleMedium
    )

    BasicRadioButton(
        isSelected = themeState.useDynamicColor,
        text = stringResource(R.string.yes),
        action = { onAction.invoke(ViewAction.ChangeDynamicColor(true)) }
    )

    BasicRadioButton(
        isSelected = !themeState.useDynamicColor,
        text = stringResource(R.string.no),
        action = { onAction.invoke(ViewAction.ChangeDynamicColor(false)) }
    )
}

@Composable
private fun ColorOptions(
    themeState: ThemeState,
    onAction: (ViewAction) -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
        Text(
            text = stringResource(R.string.color),
            style = MaterialTheme.typography.titleLarge
        )

        BasicRadioButton(
            isSelected = themeState.colorState == ColorState.DefaultBlue,
            text = stringResource(R.string.default_blue),
            action = { onAction.invoke(ViewAction.ChangeColorScheme(ColorState.DefaultBlue)) }
        )

        BasicRadioButton(
            isSelected = themeState.colorState == ColorState.Green,
            text = stringResource(R.string.green),
            action = { onAction.invoke(ViewAction.ChangeColorScheme(ColorState.Green)) }
        )

        BasicRadioButton(
            isSelected = themeState.colorState == ColorState.Orange,
            text = stringResource(R.string.orange),
            action = { onAction.invoke(ViewAction.ChangeColorScheme(ColorState.Orange)) }
        )
    }
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


