package com.alexeymerov.radiostations.presentation.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ColorState
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.DarkModeState
import com.alexeymerov.radiostations.domain.usecase.themesettings.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.presentation.theme.blue.getBlueColorScheme
import com.alexeymerov.radiostations.presentation.theme.green.getGreenColorScheme
import com.alexeymerov.radiostations.presentation.theme.orange.getOrangeColorScheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StationsAppTheme(
    themeState: ThemeState,
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    val useDarkTheme = when (themeState.darkMode) {
        DarkModeState.System -> isSystemInDarkTheme()
        DarkModeState.Dark -> true
        DarkModeState.Light -> false
    }

    val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // 31
    val context = LocalContext.current

    val colors: ColorScheme = when {
        isS && themeState.useDynamicColor && useDarkTheme -> dynamicDarkColorScheme(context)
        isS && themeState.useDynamicColor -> dynamicLightColorScheme(context)
        else -> when (themeState.colorState) {
            ColorState.Green -> getGreenColorScheme(useDarkTheme)
            ColorState.Orange -> getOrangeColorScheme(useDarkTheme)
            ColorState.DefaultBlue -> getBlueColorScheme(useDarkTheme)
        }
    }

    val typography = Typography(
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        titleSmall = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
    )

    systemUiController.setStatusBarColor(color = animateColor(colors.surface))

    MaterialTheme(
        colorScheme = colors.withAnimation(), //topbar animation somehow looks different, fix later
        typography = typography,
        content = content
    )
}

@Composable
fun animateColor(targetColor: Color) = animateColorAsState(
    targetValue = targetColor,
    animationSpec = tween(500),
    label = ""
).value

@Composable
fun ColorScheme.withAnimation() = copy(
    primary = animateColor(primary),
    onPrimary = animateColor(onPrimary),
    primaryContainer = animateColor(primaryContainer),
    onPrimaryContainer = animateColor(onPrimaryContainer),
    secondary = animateColor(secondary),
    onSecondary = animateColor(onSecondary),
    secondaryContainer = animateColor(secondaryContainer),
    onSecondaryContainer = animateColor(onSecondaryContainer),
    tertiary = animateColor(tertiary),
    onTertiary = animateColor(onTertiary),
    tertiaryContainer = animateColor(tertiaryContainer),
    onTertiaryContainer = animateColor(onTertiaryContainer),
    error = animateColor(error),
    errorContainer = animateColor(errorContainer),
    onError = animateColor(onError),
    onErrorContainer = animateColor(onErrorContainer),
    background = animateColor(background),
    onBackground = animateColor(onBackground),
    surface = animateColor(surface),
    onSurface = animateColor(onSurface),
    surfaceVariant = animateColor(surfaceVariant),
    onSurfaceVariant = animateColor(onSurfaceVariant),
    outline = animateColor(outline),
    inverseOnSurface = animateColor(inverseOnSurface),
    inverseSurface = animateColor(inverseSurface),
    inversePrimary = animateColor(inversePrimary),
    surfaceTint = animateColor(surfaceTint),
    outlineVariant = animateColor(outlineVariant),
    scrim = animateColor(scrim)
)
