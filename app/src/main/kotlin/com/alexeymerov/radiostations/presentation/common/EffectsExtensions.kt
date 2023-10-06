package com.alexeymerov.radiostations.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CallOnDispose(action: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
            action.invoke()
        }
    }
}

@Composable
fun CallOnLaunch(action: () -> Unit) {
    LaunchedEffect(Unit) {
        action.invoke()
    }
}