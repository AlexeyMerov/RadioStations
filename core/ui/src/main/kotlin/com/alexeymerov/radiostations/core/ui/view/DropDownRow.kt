package com.alexeymerov.radiostations.core.ui.view

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.common.EMPTY

@Composable
fun DropDownRow(@DrawableRes iconId: Int, @StringRes stringId: Int, action: () -> Unit) {
    Row(modifier = Modifier
        .clickable { action.invoke() }
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconId),
            modifier = Modifier.alpha(0.85f),
            contentDescription = String.EMPTY
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(stringId),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
        )
    }
}