package com.alexeymerov.radiostations.feature.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.maxDialogHeight
import com.alexeymerov.radiostations.core.ui.extensions.maxDialogWidth
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel

@Composable
internal fun ThemeSettings(
    themeState: ThemeSettingsUseCase.ThemeState,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    var needShowThemeDialog by rememberSaveable { mutableStateOf(false) }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00639C),
                        Color(0xFF1D6C30),
                        Color(0xFF954A05),
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
    themeState: ThemeSettingsUseCase.ThemeState,
    onDismiss: () -> Unit,
    onViewAction: (SettingsViewModel.ViewAction) -> Unit
) {
    val config = LocalConfiguration.current

    AlertDialog(
        modifier = Modifier
            .sizeIn(
                maxWidth = config.maxDialogWidth(),
                maxHeight = config.maxDialogHeight()
            ),
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                modifier = Modifier.clickable(onClick = onDismiss),
                text = stringResource(R.string.done)
            )
        },
        title = { Text(text = stringResource(R.string.theme_settings)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                BasicText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.dark_mode), textStyle = MaterialTheme.typography.titleMedium
                )

                DarkThemeOptions(themeState.darkLightMode, onAction = onViewAction)

                //val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S check is show
                BasicText(
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.dynamic_color),
                    textStyle = MaterialTheme.typography.titleMedium
                )

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
    darkLightMode: ThemeSettingsUseCase.DarkLightMode,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    BasicRadioButton(
        isSelected = darkLightMode == ThemeSettingsUseCase.DarkLightMode.SYSTEM,
        text = stringResource(R.string.system),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeDarkMode(ThemeSettingsUseCase.DarkLightMode.SYSTEM)) }
    )

    BasicRadioButton(
        isSelected = darkLightMode == ThemeSettingsUseCase.DarkLightMode.LIGHT,
        text = stringResource(R.string.light),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeDarkMode(ThemeSettingsUseCase.DarkLightMode.LIGHT)) }
    )

    BasicRadioButton(
        isSelected = darkLightMode == ThemeSettingsUseCase.DarkLightMode.DARK,
        text = stringResource(R.string.dark),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeDarkMode(ThemeSettingsUseCase.DarkLightMode.DARK)) }
    )

    BasicRadioButton(
        isSelected = darkLightMode == ThemeSettingsUseCase.DarkLightMode.NIGHT,
        text = stringResource(R.string.night),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeDarkMode(ThemeSettingsUseCase.DarkLightMode.NIGHT)) }
    )
}

@Composable
private fun DynamicColorOptions(
    useDynamicColor: Boolean,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    BasicRadioButton(
        isSelected = useDynamicColor,
        text = stringResource(R.string.yes),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeDynamicColor(true)) }
    )

    BasicRadioButton(
        isSelected = !useDynamicColor,
        text = stringResource(R.string.no),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeDynamicColor(false)) }
    )
}

@Composable
private fun ColorOptions(
    colorTheme: ThemeSettingsUseCase.ColorTheme,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    Text(
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
        text = stringResource(R.string.color),
        style = MaterialTheme.typography.titleMedium
    )

    BasicRadioButton(
        isSelected = colorTheme == ThemeSettingsUseCase.ColorTheme.DEFAULT_BLUE,
        text = stringResource(R.string.default_blue),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeColorScheme(ThemeSettingsUseCase.ColorTheme.DEFAULT_BLUE)) }
    )

    BasicRadioButton(
        isSelected = colorTheme == ThemeSettingsUseCase.ColorTheme.GREEN,
        text = stringResource(R.string.green),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeColorScheme(ThemeSettingsUseCase.ColorTheme.GREEN)) }
    )

    BasicRadioButton(
        isSelected = colorTheme == ThemeSettingsUseCase.ColorTheme.ORANGE,
        text = stringResource(R.string.orange),
        action = { onAction.invoke(SettingsViewModel.ViewAction.ChangeColorScheme(ThemeSettingsUseCase.ColorTheme.ORANGE)) }
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
            .wrapContentWidth()
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
