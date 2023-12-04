package com.alexeymerov.radiostations.core.ui.remembers

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import timber.log.Timber

@Composable
fun rememberGalleyPicker(onComplete: (Uri) -> Unit) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
        if (uri != null) onComplete.invoke(uri)
    }
)

@Composable
fun rememberTakePicture(onComplete: () -> Unit) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(),
    onResult = { result ->
        Timber.d("cameraLauncher: $result")
        if (result) onComplete.invoke()
    }
)
