package com.alexeymerov.radiostations.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StationsAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val isS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // 31
    colors = when {
        isS && useDarkTheme -> dynamicDarkColorScheme(context)
        isS -> dynamicLightColorScheme(context)
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    systemUiController.setSystemBarsColor(color = colors.surface)

    val typography = Typography(
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        titleSmall = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}