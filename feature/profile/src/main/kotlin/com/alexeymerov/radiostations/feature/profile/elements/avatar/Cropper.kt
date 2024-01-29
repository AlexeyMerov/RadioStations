package com.alexeymerov.radiostations.feature.profile.elements.avatar

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.maxDialogHeight
import com.alexeymerov.radiostations.core.ui.extensions.maxDialogWidth
import com.alexeymerov.radiostations.core.ui.extensions.toPx
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.profile.helpers.rotate
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.CropType
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun Cropper(
    imageToCrop: ImageBitmap,
    onUpdateImage: (ImageBitmap) -> Unit,
    onClose: (Bitmap?) -> Unit
) {
    val config = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()

    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var doCropAction by remember { mutableStateOf(false) }

    croppedImage?.let {
        onClose.invoke(it.asAndroidBitmap())
        croppedImage = null
        onClose.invoke(null)
    }

    Dialog(onDismissRequest = { onClose.invoke(null) }) {
        Column(
            modifier = Modifier
                .sizeIn(
                    maxWidth = config.maxDialogWidth(),
                    maxHeight = config.maxDialogHeight()
                )
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(R.string.select_area)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                ImageCropper(
                    modifier = Modifier,
                    imageBitmap = imageToCrop,
                    crop = doCropAction,
                    contentDescription = null,
                    cropStyle = CropDefaults.style(
                        drawOverlay = false,
                        drawGrid = false,
                        overlayColor = MaterialTheme.colorScheme.secondary, // grid lines color
                        handleColor = MaterialTheme.colorScheme.primary, // angle handles color
                    ),
                    cropProperties = CropDefaults.properties(
                        cropType = CropType.Static,
                        handleSize = 16.dp.toPx(),
                        cropOutlineProperty = CropOutlineProperty(
                            outlineType = OutlineType.Rect,
                            cropOutline = RectCropShape(0, "Rect")
                        ),
                        aspectRatio = aspectRatios[3].aspectRatio,
                        fixedAspectRatio = true,
                        maxZoom = 5f
                    ),
                    onCropStart = { Timber.d("onCrop Start") }, // if freezing, consider to add a loader
                    onCropSuccess = {
                        Timber.d("onCrop Success")
                        croppedImage = it
                        doCropAction = false
                    }
                )

                Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                    ImageModificationIconWithAction(
                        icon = Icons.AutoMirrored.Rounded.RotateLeft,
                        action = {
                            coroutineScope.launch {
                                onUpdateImage.invoke(imageToCrop.rotate(-90f))
                            }
                        }
                    )

                    ImageModificationIconWithAction(
                        icon = Icons.AutoMirrored.Rounded.RotateRight,
                        action = {
                            coroutineScope.launch {
                                onUpdateImage.invoke(imageToCrop.rotate(90f))
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onClose.invoke(null) }) {
                    Text(text = stringResource(R.string.cancel))
                }
                Button(onClick = { doCropAction = true }) {
                    Text(text = stringResource(R.string.crop))
                }
            }
        }
    }
}