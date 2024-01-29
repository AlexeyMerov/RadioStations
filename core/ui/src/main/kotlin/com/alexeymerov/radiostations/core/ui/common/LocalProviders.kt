package com.alexeymerov.radiostations.core.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbar = compositionLocalOf<SnackbarHostState> { error("SnackbarHostState not found") }

val LocalConnectionStatus = compositionLocalOf<Boolean> { error("ConnectionStatus not found") }

val LocalPlayerVisibility = compositionLocalOf<Boolean> { error("PlayerVisibility not found") }

val LocalDarkMode = compositionLocalOf<Boolean> { error("DarkMode not found") }

val LocalNightMode = compositionLocalOf<Boolean> { error("NightMode not found") }