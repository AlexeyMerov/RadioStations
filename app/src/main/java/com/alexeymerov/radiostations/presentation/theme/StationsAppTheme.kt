package com.alexeymerov.radiostations.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StationsAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    colors = when {
//        isS && useDarkTheme -> dynamicDarkColorScheme(context)
//        isS -> dynamicLightColorScheme(context)
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    systemUiController.setSystemBarsColor(color = colors.surface)

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}