package com.alexeymerov.radiostations.core.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbar = compositionLocalOf<SnackbarHostState> { error("SnackbarHostState not found") }