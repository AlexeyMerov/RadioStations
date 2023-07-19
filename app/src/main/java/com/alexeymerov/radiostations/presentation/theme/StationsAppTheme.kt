package com.alexeymerov.radiostations.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StationsAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val context = LocalContext.current
//    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
//        if (useDarkTheme) dynamicDarkColorScheme(context) // todo learn more
//        else dynamicLightColorScheme(context)
//    }

    val colors: ColorScheme
    val systemUiController = rememberSystemUiController()
    if (useDarkTheme) {
        colors = DarkColorScheme
        systemUiController.setSystemBarsColor(color = colors.secondaryContainer)
    } else {
        colors = LightColorScheme
        systemUiController.setSystemBarsColor(color = colors.secondary)
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}