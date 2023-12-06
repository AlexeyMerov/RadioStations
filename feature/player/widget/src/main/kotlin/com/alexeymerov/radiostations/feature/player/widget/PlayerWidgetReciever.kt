package com.alexeymerov.radiostations.feature.player.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalGlanceApi::class)
@AndroidEntryPoint
class PlayerWidgetReciever : GlanceAppWidgetReceiver() {

    @Inject
    lateinit var audioUseCase: AudioUseCase

    override val glanceAppWidget = PlayerWidget()

    private val coroutineScope = CoroutineScope(coroutineContext + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("PlayerWidgetReciever - onReceive: $intent")
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            setupData(context)
        }
    }

    private fun setupData(context: Context) {
        coroutineScope.launch {
            val currentMediaItem = audioUseCase.getLastPlayingMediaItem().first()

            GlanceAppWidgetManager(context)
                .getGlanceIds(PlayerWidget::class.java)
                .forEach {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[PlayerWidget.prefTitleKey] = currentMediaItem?.title.toString()
                        }
                    }
                    glanceAppWidget.update(context, it)
                }
        }
    }
}