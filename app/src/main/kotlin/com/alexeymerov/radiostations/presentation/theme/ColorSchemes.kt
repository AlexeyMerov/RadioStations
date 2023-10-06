package com.alexeymerov.radiostations.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightColorScheme = lightColorScheme(
    primary = main_200,
    primaryContainer = main_700,

    secondary = main_500,
    secondaryContainer = main_700,

    tertiary = second_200,
    tertiaryContainer = second_700,
)

val DarkColorScheme = darkColorScheme(
    tertiary = second_200,
    tertiaryContainer = second_700,

    surface = grey
)