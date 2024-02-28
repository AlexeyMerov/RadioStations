package com.alexeymerov.radiostations.presentation.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ColorTheme
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.DarkLightMode
import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import com.alexeymerov.radiostations.core.ui.common.LocalDarkMode
import com.alexeymerov.radiostations.core.ui.common.LocalNightMode
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

    val useDarkTheme = when (themeState.darkLightMode) {
        DarkLightMode.SYSTEM -> isSystemInDarkTheme()
        DarkLightMode.LIGHT -> false
        DarkLightMode.DARK, DarkLightMode.NIGHT -> true
    }

    val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // 31
    val context = LocalContext.current

    var colors: ColorScheme = when {
        isS && themeState.useDynamicColor && useDarkTheme -> dynamicDarkColorScheme(context)
        isS && themeState.useDynamicColor -> dynamicLightColorScheme(context)
        else -> when (themeState.colorTheme) {
            ColorTheme.GREEN -> getGreenColorScheme(useDarkTheme)
            ColorTheme.ORANGE -> getOrangeColorScheme(useDarkTheme)
            ColorTheme.DEFAULT_BLUE -> getBlueColorScheme(useDarkTheme)
        }
    }

    val isDarkMode = themeState.darkLightMode == DarkLightMode.DARK
    val isNightMode = themeState.darkLightMode == DarkLightMode.NIGHT

    if (isNightMode) {
        colors = colors.copy(
            surface = Color.Black,
            background = Color.Black,
//            scrim ??
        )
    }

    val typography = Typography(
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        titleSmall = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
    )

    systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = !useDarkTheme)

    MaterialTheme(
        colorScheme = colors.withAnimation(),
        typography = typography,
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp)), // at the moment only for DropDownMenu

    ) {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation)
        )

        CompositionLocalProvider(
            LocalDarkMode provides isDarkMode,
            LocalNightMode provides isNightMode,
        ) {
            content.invoke()
        }
    }
}

@Composable
fun animateColor(targetColor: Color) = animateColorAsState(
    targetValue = targetColor,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "animateColor"
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
