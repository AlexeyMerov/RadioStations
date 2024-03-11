package com.alexeymerov.radiostations.feature.player.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.alexeymerov.radiostations.core.common.base64ToBitmap
import com.alexeymerov.radiostations.core.ui.R
import timber.log.Timber


class PlayerWidget : GlanceAppWidget() {

    companion object {
        val prefTitleKey = stringPreferencesKey("title")
        val prefImageBase64 = stringPreferencesKey("imageBase64")
        val prefIsPlaying = booleanPreferencesKey("isPlaying")

        private val ROW_SMALL = DpSize(40.dp, 40.dp)
        private val ROW_MEDIUM = DpSize(120.dp, 40.dp)
        private val ROW_LARGE = DpSize(200.dp, 40.dp)
        private val ROW_EXTRA_LARGE = DpSize(280.dp, 40.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(ROW_SMALL, ROW_MEDIUM, ROW_LARGE, ROW_EXTRA_LARGE)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CompositionLocalProvider(LocalContext provides context) {
                    val currentSize = LocalSize.current
                    Timber.d("currentSize $currentSize")

                    val image = currentState(prefImageBase64)?.base64ToBitmap()
                    val showTitle = currentSize == ROW_LARGE || currentSize == ROW_EXTRA_LARGE

//                    val isPlaying = currentState(prefIsPlaying) ?: false
//                    val buttonOverImage = currentSize == ROW_SMALL || currentSize == ROW_LARGE

                    Row(
                        modifier = GlanceModifier
                            .run {
                                if (currentSize == ROW_EXTRA_LARGE) {
                                    background(GlanceTheme.colors.background)
                                } else this
                            }
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = GlanceModifier
                                .wrapContentSize()
                                .cornerRadius(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = GlanceModifier
                                    .run {
                                        if (currentSize == ROW_SMALL) {
                                            fillMaxWidth().wrapContentHeight()
                                        } else {
                                            fillMaxHeight().wrapContentWidth()
                                        }
                                    },
                                provider = image?.let { ImageProvider(it) } ?: ImageProvider(R.drawable.icon_radio),
                                contentDescription = null
                            )

//                            if (buttonOverImage) PlayIcon(isPlaying = isPlaying)
                        }

                        if (showTitle) {
                            Spacer(GlanceModifier.width(8.dp))

                            Box(
                                modifier = GlanceModifier
                                    .background(GlanceTheme.colors.background)
                                    .wrapContentSize()
                                    .cornerRadius(8.dp),
                            ) {
                                Text(
                                    modifier = GlanceModifier.padding(8.dp),
                                    text = currentState(prefTitleKey) ?: "Title",
                                    style = TextStyle(
                                        fontSize = TextUnit(16f, TextUnitType.Sp),
                                        color = GlanceTheme.colors.onBackground
                                    )
                                )
                            }

                        }

//                        if (!buttonOverImage) {
//                            Spacer(modifier = GlanceModifier.defaultWeight())
//                            PlayIcon(isPlaying = isPlaying)
//                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayIcon(
    modifier: GlanceModifier = GlanceModifier,
    isPlaying: Boolean
) {
    val context = LocalContext.current
    Image(
        modifier = modifier
            .size(36.dp)
            .background(GlanceTheme.colors.background.getColor(context).copy(alpha = 0.25f))
            .cornerRadius(16.dp)
            .clickable {

            },
        provider = ImageProvider(
            if (isPlaying) R.drawable.icon_stop else R.drawable.icon_play
        ),
        colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
        contentDescription = null
    )
}