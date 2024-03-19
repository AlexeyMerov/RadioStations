package com.alexeymerov.radiostations.feature.player.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alexeymerov.radiostations.feature.player.common.WidgetIntentActions
import com.alexeymerov.radiostations.feature.player.manager.MediaManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlayerStateReceiver : BroadcastReceiver(), MediaManager.Listener {

    @Inject
    lateinit var mediaServiceManager: MediaManager

    private var currentMediaAction: String? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("-> onReceive: $intent")
        val action = intent?.action ?: return

        if (action == WidgetIntentActions.PLAY_PAUSE
            || action == WidgetIntentActions.PLAY
            || action == WidgetIntentActions.STOP
        ) {
            currentMediaAction = intent.action
            mediaServiceManager.addListener(this)
            mediaServiceManager.setupPlayer()
        }
    }

    override fun onControllerInitialized() {
        Timber.d("-> onControllerInitialized: ")
        when (currentMediaAction) {
            WidgetIntentActions.PLAY -> mediaServiceManager.play()
            WidgetIntentActions.STOP -> mediaServiceManager.stop()
        }
    }
}