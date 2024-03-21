package com.alexeymerov.radiostations.feature.player.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.action.actionStartActivity
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
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.ProjectConst.DEEP_LINK_TUNE_PATTERN
import com.alexeymerov.radiostations.core.common.base64ToBitmap
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.feature.player.common.WidgetIntentActions


private val ROW_SMALL = DpSize(40.dp, 40.dp)
private val ROW_MEDIUM = DpSize(120.dp, 40.dp)
private val ROW_LARGE = DpSize(200.dp, 40.dp)
private val ROW_EXTRA_LARGE = DpSize(280.dp, 40.dp)

class PlayerWidget : GlanceAppWidget() {

    companion object {
        val prefTitleKey = stringPreferencesKey("title")
        val prefImageBase64 = stringPreferencesKey("imageBase64")
        val prefIsPlaying = booleanPreferencesKey("isPlaying")
        val prefTuneId = stringPreferencesKey("tuneId")
        val prefIsStateLoading = booleanPreferencesKey("isStateLoading")
    }

    override val sizeMode = SizeMode.Responsive(setOf(ROW_SMALL, ROW_MEDIUM, ROW_LARGE, ROW_EXTRA_LARGE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CompositionLocalProvider(LocalContext provides context) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
private fun MainContent() {
    val context = LocalContext.current
    val currentSize = LocalSize.current

    val image = currentState(PlayerWidget.prefImageBase64)?.base64ToBitmap()
    val showTitle = currentSize == ROW_LARGE || currentSize == ROW_EXTRA_LARGE

    val tuneId = currentState(PlayerWidget.prefTuneId) ?: String.EMPTY

    val isPlaying = currentState(PlayerWidget.prefIsPlaying) ?: false
    val buttonOverImage = currentSize != ROW_EXTRA_LARGE

    Row(
        modifier = GlanceModifier
            .run {
                if (currentSize == ROW_EXTRA_LARGE) {
                    background(GlanceTheme.colors.background)
                } else this
            }
            .fillMaxSize()
            .padding(8.dp)
            .clickable(
                actionStartActivity(
                    checkNotNull(context.packageManager.getLaunchIntentForPackage(context.packageName))
                        .apply {
                            action = Intent.ACTION_VIEW
                            data = "$DEEP_LINK_TUNE_PATTERN/$tuneId".toUri()
                        }
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = GlanceModifier
                .wrapContentSize()
                .cornerRadius(8.dp),
            contentAlignment = Alignment.Center
        ) {
            StationImage(image)

            if (buttonOverImage) {
                PlayIcon(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(GlanceTheme.colors.onBackground.getColor(context).copy(alpha = 0.75f)),
                    isPlaying = isPlaying
                )
            }
        }

        if (showTitle) {
            Spacer(GlanceModifier.width(8.dp))
            TitleText(modifier = GlanceModifier.width(currentSize.width / 2.5f))
        }

        if (!buttonOverImage) {
            Spacer(modifier = GlanceModifier.width(8.dp))
            PlayIcon(
                modifier = GlanceModifier
                    .size(48.dp)
                    .background(GlanceTheme.colors.onBackground),
                isPlaying = isPlaying
            )
        }
    }
}

@Composable
private fun StationImage(image: Bitmap?) {
    val currentSize = LocalSize.current
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
}

@Composable
private fun TitleText(modifier: GlanceModifier = GlanceModifier) {
    Box(
        modifier = modifier
            .background(GlanceTheme.colors.background)
            .cornerRadius(8.dp)
    ) {
        Text(
            modifier = GlanceModifier.padding(8.dp),
            text = currentState(PlayerWidget.prefTitleKey) ?: "Title",
            style = TextStyle(
                fontSize = TextUnit(16f, TextUnitType.Sp),
                color = GlanceTheme.colors.onBackground
            ),
            maxLines = 3
        )
    }
}

@Composable
private fun PlayIcon(
    modifier: GlanceModifier = GlanceModifier,
    isPlaying: Boolean
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .cornerRadius(8.dp)
            .clickable(
                if (isPlaying) {
                    actionSendBroadcast(
                        WidgetIntentActions.STOP,
                        ComponentName(context, PlayerWidgetReceiver::class.java)
                    )
                } else {
                    actionSendBroadcast(
                        WidgetIntentActions.PLAY,
                        ComponentName(context, PlayerWidgetReceiver::class.java)
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (currentState(PlayerWidget.prefIsStateLoading) == true) {
            CircularProgressIndicator(GlanceModifier.padding(8.dp))
        } else {
            Image(
                modifier = GlanceModifier,
                provider = ImageProvider(
                    if (isPlaying) R.drawable.icon_stop else R.drawable.icon_play
                ),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.background),
                contentDescription = null
            )
        }
    }
}