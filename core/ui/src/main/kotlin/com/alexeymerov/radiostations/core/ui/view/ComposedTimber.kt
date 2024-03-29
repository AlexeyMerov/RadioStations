package com.alexeymerov.radiostations.core.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import timber.log.Timber

@Composable
fun ComposedTimberD(text: String) {
    LaunchedEffect(Unit) {
        Timber.d(text)
    }
}