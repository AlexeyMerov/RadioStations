package com.alexeymerov.radiostations.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BasicText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        maxLines = 1,
        color = color,
        overflow = TextOverflow.Ellipsis
    )
}