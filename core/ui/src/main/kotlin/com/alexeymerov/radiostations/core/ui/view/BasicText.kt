package com.alexeymerov.radiostations.core.ui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize

@Composable
fun BasicText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    onTextSize: ((DpSize) -> Unit)? = null
) {
    val density = LocalDensity.current
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        maxLines = 1,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = {
            onTextSize?.invoke(
                with(density) {
                    DpSize(
                        width = it.size.width.toDp(),
                        height = it.size.height.toDp()
                    )
                }

            )
        }
    )
}