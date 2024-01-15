package com.alexeymerov.radiostations.feature.profile.elements.avatar

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.alexeymerov.radiostations.core.common.EMPTY

@Composable
fun ImageModificationIconWithAction(icon: ImageVector, action: () -> Unit) {
    IconButton(onClick = { action.invoke() }) {
        Icon(
            imageVector = icon,
            contentDescription = String.EMPTY,
            tint = Color.White
        )
    }
}

