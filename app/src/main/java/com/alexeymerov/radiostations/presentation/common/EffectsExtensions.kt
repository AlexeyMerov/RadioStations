package com.alexeymerov.radiostations.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun CallOnDispose(action: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
            action.invoke()
        }
    }
}