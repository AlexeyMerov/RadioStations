package com.alexeymerov.radiostations.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = main_200,
    onPrimary = Color.White,
    primaryContainer = main_700,
    onPrimaryContainer = Color.White,

    secondary = main_500,
    onSecondary = Color.White,
    secondaryContainer = main_700,
    onSecondaryContainer = Color.White,

    tertiary = second_200,
    onTertiary = Color.White,
    tertiaryContainer = second_700,
    onTertiaryContainer = Color.White,

    surface = main_200,
    onSurface = Color.White,

    background = Color.White,
    onBackground = Color.Black
)

val DarkColorScheme = darkColorScheme(
    primary = main_200,
    onPrimary = Color.White,
    primaryContainer = main_700,
    onPrimaryContainer = Color.White,

    secondary = main_500,
    onSecondary = Color.White,
    secondaryContainer = main_700,
    onSecondaryContainer = Color.White,

    tertiary = second_200,
    onTertiary = Color.White,
    tertiaryContainer = second_700,
    onTertiaryContainer = Color.White,

    surface = main_500,
    onSurface = Color.White,

    background = grey,
    onBackground = Color.White
)