package com.alexeymerov.radiostations.feature.player.screen.elements

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.alexeymerov.radiostations.core.ui.R

@Composable
internal fun PlayerArtwork(
    modifier: Modifier,
    imageUrl: String,
    onImageLoaded: ((Bitmap) -> Unit)? = null
) {
    var isLoaded by rememberSaveable { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val colorFilter: ColorFilter? by remember(isLoaded) {
        derivedStateOf {
            if (isLoaded) null else ColorFilter.tint(colorScheme.primary)
        }
    }

    AsyncImage(
        modifier = modifier.background(Color.White),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .crossfade(500)
            .build(),
        contentDescription = null,
        error = rememberAsyncImagePainter(R.drawable.icon_radio),
        colorFilter = colorFilter,
        onSuccess = {
            val bitmap = it.result.drawable.toBitmap()
            onImageLoaded?.invoke(bitmap)
            isLoaded = true
        },
        onError = { isLoaded = false }
    )
}