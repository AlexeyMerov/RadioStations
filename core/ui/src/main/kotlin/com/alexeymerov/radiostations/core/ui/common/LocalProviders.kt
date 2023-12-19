package com.alexeymerov.radiostations.core.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbar = compositionLocalOf<SnackbarHostState> { error("SnackbarHostState not found") }

@OptIn(ExperimentalMaterial3Api::class)
val LocalTopBarScroll = compositionLocalOf<TopAppBarScrollBehavior> { error("NestedScrollConnection not found") }