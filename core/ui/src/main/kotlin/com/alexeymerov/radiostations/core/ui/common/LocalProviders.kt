package com.alexeymerov.radiostations.core.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import timber.log.Timber

val LocalSnackbar = compositionLocalOf<SnackbarHostState> { error("SnackbarHostState not found") }

val LocalConnectionStatus = compositionLocalOf<Boolean> {
    Timber.e("ConnectionStatus not found")
    true
}

val LocalPlayerVisibility = compositionLocalOf<Boolean> {
    Timber.e("PlayerVisibility not found")
    false
}

val LocalDarkMode = compositionLocalOf<Boolean> {
    Timber.e("DarkMode not found")
    false
}

val LocalNightMode = compositionLocalOf<Boolean> {
    Timber.e("NightMode not found")
    false
}