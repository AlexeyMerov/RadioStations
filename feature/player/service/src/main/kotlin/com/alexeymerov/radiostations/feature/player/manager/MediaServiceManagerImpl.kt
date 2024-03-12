package com.alexeymerov.radiostations.feature.player.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.feature.player.common.mapToMediaItem
import com.alexeymerov.radiostations.feature.player.service.PlayerService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class MediaServiceManagerImpl @Inject constructor(
    @ApplicationContext val context: Context
) : MediaServiceManager {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var currentAudioItem: AudioItemDto? = null

    override fun getStationRouteIfExist(intent: Intent): String? {
        val url = intent.getStringExtra(INTENT_KEY_AUDIO_URL)
        val title = intent.getStringExtra(INTENT_KEY_AUDIO_TITLE)

        Timber.d("getStationRouteIfExist url = $url ## title = $title")

        return if (url == null || title == null) {
            null
        } else {
            Timber.d("getStationRouteIfExist valid")
            Screens.Player(Tabs.Browse.route).createRoute(
                rawUrl = url,
                stationName = title
            )
        }
    }

    override fun setupPlayer() {
        Timber.d("-> setupPlayer: ")
        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.let { future ->
            val listener = { mediaController = future.get() }
            future.addListener(listener, MoreExecutors.directExecutor())
        }
    }

    override fun processNewAudioItem(item: AudioItemDto) {
        Timber.d("processCurrentAudioItem $item")
        currentAudioItem = item

        mediaController?.also { controller ->
            if (item.directUrl != controller.currentMediaItem?.mediaId) {
                controller.setMediaItem(mapToMediaItem(item))
                controller.prepare()
            }
        }

        createDynamicShortcut(item)
    }

    private fun createDynamicShortcut(item: AudioItemDto) {
        Timber.d("-> createDynamicShortcut: $item")

        val shortLabel = if (item.title.length > 12) "${item.title.substring(0, 10)}..." else item.title
        var longLabel = item.title
        item.subTitle?.let {
            longLabel = "$longLabel (${item.subTitle})"
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            val bundle = Bundle().apply {
                putString(INTENT_KEY_AUDIO_URL, item.parentUrl)
                putString(INTENT_KEY_AUDIO_TITLE, item.title)
            }
            putExtras(bundle)
        }

        if (intent != null) {
            val shortcut = ShortcutInfoCompat.Builder(context, DYNAMIC_SHORTCUT_ID)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setDisabledMessage("Not")
                .setIcon(IconCompat.createWithResource(context, R.drawable.icon_radio))
                .setIntent(intent)
                .build()

            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
    }

    override fun processPlayerState(state: PlayerState) {
        Timber.d("processPlayerState - playerState $state" + " == currentMediaItem ${mediaController?.currentMediaItem}")
        mediaController?.also { controller ->
            when (state) {
                PlayerState.EMPTY -> processEmptyState(controller)
                PlayerState.PLAYING -> {
                    if (!controller.isPlaying) {
                        currentAudioItem
                            .takeIf { controller.currentMediaItem == null }
                            ?.let { processNewAudioItem(it) }

                        controller.playWhenReady = true
                    }
                }

                PlayerState.STOPPED -> controller.pause()
                else -> {
                    /* no action needed */
                }
            }
        }
    }

    private fun processEmptyState(controller: MediaController) {
        Timber.d("-> processEmptyState: ")
        controller.stop()
        controller.clearMediaItems()
        ShortcutManagerCompat.disableShortcuts(
            /* context = */ context,
            /* shortcutIds = */ listOf(DYNAMIC_SHORTCUT_ID),
            /* disabledMessage = */ "Not available" // we can't remove pinned icon
        )
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }

    override fun onStop() {
        Timber.d("-> onStop: ")
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }

    private companion object {
        const val DYNAMIC_SHORTCUT_ID = "latest_station_static_id"

        const val INTENT_KEY_AUDIO_URL = "parent_url"
        const val INTENT_KEY_AUDIO_TITLE = "audio_title"
    }
}