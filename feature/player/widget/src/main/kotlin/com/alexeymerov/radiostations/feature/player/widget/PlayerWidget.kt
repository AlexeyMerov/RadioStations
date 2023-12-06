package com.alexeymerov.radiostations.feature.player.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.alexeymerov.radiostations.core.ui.R


class PlayerWidget : GlanceAppWidget() {

    companion object {
        val prefTitleKey = stringPreferencesKey("title")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val title = prefs[prefTitleKey]

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.background)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.currently_playing),
                        style = TextDefaults.defaultTextStyle.copy(
                            color = GlanceTheme.colors.secondary
                        ),
                        modifier = GlanceModifier.padding(top = 8.dp)
                    )

                    Text(text = title ?: "Empty", modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp))
                }
            }
        }
    }

}