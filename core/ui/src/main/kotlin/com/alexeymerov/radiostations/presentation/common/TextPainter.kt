package com.alexeymerov.radiostations.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

class TextPainter(
    private val containerColor: Color = Color.Black,
    private val containerSize: Size = Size.Unspecified,
    textStyle: TextStyle = TextStyle(color = Color.White, fontSize = 20.sp),
    textMeasurer: TextMeasurer,
    text: String
) : Painter() {

    private val textLayoutResult: TextLayoutResult = textMeasurer.measure(
        text = AnnotatedString(text),
        style = textStyle
    )

    override val intrinsicSize: Size get() = containerSize

    override fun DrawScope.onDraw() {
        drawRect(color = containerColor)

        val textSize = textLayoutResult.size
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                (this.size.width - textSize.width) / 2f,
                (this.size.height - textSize.height) / 2f
            )
        )
    }
}

@Composable
fun rememberTextPainter(
    containerColor: Color = Color.Black,
    textStyle: TextStyle = TextStyle(color = Color.White, fontSize = 20.sp),
    text: String,
): TextPainter {
    val textMeasurer = rememberTextMeasurer()
    return remember(text) {
        TextPainter(
            containerColor = containerColor,
            textStyle = textStyle,
            textMeasurer = textMeasurer,
            text = text
        )
    }
}