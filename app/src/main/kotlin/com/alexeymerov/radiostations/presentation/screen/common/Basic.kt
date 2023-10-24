package com.alexeymerov.radiostations.presentation.screen.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BasicText(modifier: Modifier = Modifier, text: String, textStyle: TextStyle = MaterialTheme.typography.titleMedium) {
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}