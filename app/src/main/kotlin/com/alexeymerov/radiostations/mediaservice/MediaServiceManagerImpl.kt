package com.alexeymerov.radiostations.mediaservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.feature.player.service.PlayerService
import com.alexeymerov.radiostations.feature.player.service.mapToMediaItem
import com.alexeymerov.radiostations.presentation.MainActivity
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import timber.log.Timber
import javax.inject.Inject

class MediaServiceManagerImpl @Inject constructor() : MediaServiceManager {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    override fun getStationRouteIfExist(intent: Intent): String? {
        return intent.getStringExtra(INTENT_KEY_URL)?.let {
            Screens.Player(Tabs.Browse.route).createRoute(it)
        }
    }

    override fun setupPlayer(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.let { future ->
            val listener = { mediaController = future.get() }
            future.addListener(listener, MoreExecutors.directExecutor())
        }
    }

    override fun processCurrentAudioItem(context: Context, item: AudioItemDto) {
        Timber.d("processCurrentAudioItem $item")
        mediaController?.also { controller ->
            if (item.directUrl != controller.currentMediaItem?.mediaId) {
                controller.setMediaItem(mapToMediaItem(item))
                controller.prepare()
            }
        }

        createDynamicShortcut(context, item)
    }

    private fun createDynamicShortcut(context: Context, item: AudioItemDto) {
        val shortLabel = if (item.title.length > 12) "${item.title.substring(0, 10)}..." else item.title
        val longLabel = "${item.title} (${item.subTitle})"

        val shortcut = ShortcutInfoCompat.Builder(context, DYNAMIC_SHORTCUT_ID)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setDisabledMessage("Not")
            .setIcon(IconCompat.createWithResource(context, R.drawable.icon_radio))
            .setIntent(
                Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    val bundle = Bundle()
                    bundle.putString(INTENT_KEY_URL, item.parentUrl)
                    putExtras(bundle)
                }
            )
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }

    override fun processPlayerState(context: Context, state: AudioUseCase.PlayerState, currentMedia: AudioItemDto?) {
        Timber.d("processPlayerState - playerState $state" + " == currentMediaItem ${mediaController?.currentMediaItem}")
        mediaController?.also { controller ->
            when (state) {
                AudioUseCase.PlayerState.EMPTY -> processEmptyState(controller, context)
                AudioUseCase.PlayerState.PLAYING -> {
                    if (!controller.isPlaying) {
                        if (controller.currentMediaItem == null && currentMedia != null) {
                            processCurrentAudioItem(context, currentMedia)
                        }

                        controller.playWhenReady = true
                    }
                }

                AudioUseCase.PlayerState.STOPPED -> controller.pause()
                AudioUseCase.PlayerState.LOADING -> { /* no action needed */
                }
            }
        }
    }

    private fun processEmptyState(controller: MediaController, context: Context) {
        controller.stop()
        controller.clearMediaItems()
        ShortcutManagerCompat.disableShortcuts(
            /* context = */ context,
            /* shortcutIds = */ listOf(DYNAMIC_SHORTCUT_ID),
            /* disabledMessage = */ "Not available" // kinda wierd we can't remove pinned icon
        )
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }

    override fun onStop() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }

    private companion object {
        const val DYNAMIC_SHORTCUT_ID = "latest_station_static_id"
        const val INTENT_KEY_URL = "parent_url"
    }
}