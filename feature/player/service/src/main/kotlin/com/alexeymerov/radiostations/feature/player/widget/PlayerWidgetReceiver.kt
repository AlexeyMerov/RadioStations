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
import com.alexeymerov.radiostations.feature.player.broadcast.PlayerStateReceiver
import com.alexeymerov.radiostations.feature.player.common.WidgetIntentActions
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
        Timber.d("onReceive: $intent \n--- data: ${intent.data}")
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> updateData(context)
            WidgetIntentActions.PLAY, WidgetIntentActions.STOP -> {
                stateLoadingData(context) {
                    context.sendBroadcast(
                        Intent(context, PlayerStateReceiver::class.java).apply {
                            action = intent.action
                        }
                    )
                }
            }
        }
    }

    private fun updateData(context: Context) {
        Timber.d("-> updateData: ")
        coroutineScope.launch {
            val currentMediaItem = playingUseCase.getLastPlayingMediaItem().first()
            val isPlaying = playingUseCase.getPlayerState().first() is PlayerState.Playing

            GlanceAppWidgetManager(context)
                .getGlanceIds(PlayerWidget::class.java)
                .forEach {
                    updateAppWidgetState(context, it) { pref ->
                        pref[PlayerWidget.prefTitleKey] = currentMediaItem?.title.orEmpty()
                        pref[PlayerWidget.prefImageBase64] = currentMediaItem?.imageBase64.orEmpty()
                        pref[PlayerWidget.prefIsPlaying] = isPlaying
                        pref[PlayerWidget.prefTuneId] = currentMediaItem?.tuneId.orEmpty()
                        pref[PlayerWidget.prefIsStateLoading] = false
                    }
                    glanceAppWidget.update(context, it)
                }
        }
    }

    private fun stateLoadingData(context: Context, onUpdated: () -> Unit) {
        Timber.d("-> stateLoadingData: ")
        coroutineScope.launch {
            GlanceAppWidgetManager(context)
                .getGlanceIds(PlayerWidget::class.java)
                .forEach {
                    updateAppWidgetState(context, it) { pref ->
                        pref[PlayerWidget.prefIsStateLoading] = true
                    }
                    glanceAppWidget.update(context, it)
                }

            onUpdated.invoke()
        }
    }
}