package com.alexeymerov.radiostations.feature.category.elements

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.alexeymerov.radiostations.core.common.DarkLightMode
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.common.LocalPlayerVisibility
import com.alexeymerov.radiostations.core.ui.common.LocalTheme
import com.alexeymerov.radiostations.core.ui.extensions.setIf
import com.alexeymerov.radiostations.core.ui.extensions.shimmerEffect
import com.alexeymerov.radiostations.feature.category.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun ShowMap(
    itemsWithBounds: Pair<LatLngBounds, List<CategoryItemDto>>,
    onAudioClick: (CategoryItemDto) -> Unit
) {
    val localTheme = LocalTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val mapStyle = when (localTheme.darkLightMode) {
        DarkLightMode.NIGHT -> R.raw.map_style_night
        DarkLightMode.DARK -> R.raw.map_style_dark
        DarkLightMode.SYSTEM -> if (isSystemInDarkTheme()) R.raw.map_style_dark else null
        else -> null
    }

    var isMapLoading by remember { mutableStateOf(true) }
    val mapStyleOptions = if (mapStyle != null) MapStyleOptions.loadRawResourceStyle(context, mapStyle) else null

    val bottomPadding by animateDpAsState(targetValue = if (LocalPlayerVisibility.current) 64.dp else 8.dp, label = String.EMPTY)
    val cameraPositionState = rememberCameraPositionState()
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding)
            .clip(RoundedCornerShape(16.dp))
            .setIf(isMapLoading) { shimmerEffect() },
        googleMapOptionsFactory = {
            GoogleMapOptions().backgroundColor(Color.Gray.toArgb())
        },
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false
        ),
        properties = MapProperties(mapStyleOptions = mapStyleOptions),
        onMapLoaded = {
            scope.launch {
                delay(1000)
                isMapLoading = false
            }
        }
    ) {
        itemsWithBounds.second.forEach { item ->
            val latitude = item.latitude
            val longitude = item.longitude

            if (latitude != null && longitude != null) {
                val markerState = rememberMarkerState(
                    key = item.text,
                    position = LatLng(latitude, longitude)
                )

                var bitmapDescriptor by remember { mutableStateOf<BitmapDescriptor?>(null) }

                Marker(
                    state = markerState,
                    title = item.text,
                    icon = bitmapDescriptor,
                    onClick = {
                        onAudioClick.invoke(item)
                        return@Marker true
                    }
                )

                LoadIconForMapPin(
                    image = item.image,
                    onLoaded = { bitmapDescriptor = it }
                )
            }
        }
    }

    LaunchedEffect(itemsWithBounds) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(itemsWithBounds.first, 100)
        )
    }
}

@Composable
private fun LoadIconForMapPin(
    image: String?,
    onLoaded: (BitmapDescriptor) -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader.Builder(context).build() }
    val imageRequest = remember { ImageRequest.Builder(context) }
    val transformation = remember { CircleCropTransformation() }

    DisposableEffect(image) {
        val request = imageRequest
            .data(image)
            .transformations(transformation)
            .target(
                onSuccess = {
                    onLoaded.invoke(BitmapDescriptorFactory.fromBitmap(it.toBitmap()))
                }
            )
            .build()

        imageLoader.enqueue(request)

        onDispose {
            imageLoader.shutdown()
        }
    }
}