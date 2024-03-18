package com.alexeymerov.radiostations.feature.player.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalGlanceApi::class)
@AndroidEntryPoint
class PlayerWidgetReceiver : GlanceAppWidgetReceiver() {

    @Inject
    lateinit var playingUseCase: PlayingUseCase

    override val glanceAppWidget = PlayerWidget()

    private val coroutineScope = CoroutineScope(coroutineContext + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("onReceive: $intent")
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            Timber.d("onReceive if inside")
            updateData(context)
        }
    }

    private fun updateData(context: Context) {
        coroutineScope.launch {
            val currentMediaItem = playingUseCase.getLastPlayingMediaItem().first()
            val isPlaying = playingUseCase.getPlayerState().first() is PlayerState.Playing

            GlanceAppWidgetManager(context)
                .getGlanceIds(PlayerWidget::class.java)
                .forEach {
                    Timber.d("updateData")
                    Timber.d("updateData: Base64 ${currentMediaItem?.imageBase64}")
                    updateAppWidgetState(context, it) { pref ->
                        pref[PlayerWidget.prefTitleKey] = currentMediaItem?.title.orEmpty()
                        pref[PlayerWidget.prefImageBase64] = currentMediaItem?.imageBase64.orEmpty()
                        pref[PlayerWidget.prefIsPlaying] = isPlaying
                        pref[PlayerWidget.prefTuneId] = currentMediaItem?.tuneId.orEmpty()
                    }
                    glanceAppWidget.update(context, it)
                }
        }
    }
}