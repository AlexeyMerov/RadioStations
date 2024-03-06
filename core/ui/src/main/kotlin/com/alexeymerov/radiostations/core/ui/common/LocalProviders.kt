package com.alexeymerov.radiostations.core.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.alexeymerov.radiostations.core.common.ThemeState
import timber.log.Timber

val LocalSnackbar = staticCompositionLocalOf<SnackbarHostState> { error("SnackbarHostState not found") }

val LocalConnectionStatus = compositionLocalOf<Boolean> {
    Timber.e("ConnectionStatus not found")
    true
}

val LocalPlayerVisibility = compositionLocalOf<Boolean> {
    Timber.e("PlayerVisibility not found")
    false
}

val LocalTheme = compositionLocalOf<ThemeState> {
    Timber.e("LocalTheme not found")
    ThemeState()
}

val LocalTopbar = staticCompositionLocalOf<(TopBarState) -> Unit> {
    Timber.e("LocalTopbar not found")
    return@staticCompositionLocalOf {}
}

