package com.alexeymerov.radiostations.feature.player.manager

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.feature.player.common.mapToMediaItem
import com.alexeymerov.radiostations.feature.player.service.PlayerService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class MediaManagerImpl @Inject constructor(
    @ApplicationContext val context: Context
) : MediaManager {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var currentAudioItem: AudioItemDto? = null

    override fun setupPlayer() {
        Timber.d("-> setupPlayer: $mediaController")
        if (mediaController != null) return

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
    }

    override fun processPlayerState(state: PlayerState) {
        Timber.d("processPlayerState - playerState $state ## mediaController $mediaController ## currentMediaItem (${mediaController?.mediaItemCount}) ${mediaController?.currentMediaItem}")
        mediaController?.also { controller ->
            when {
                state is PlayerState.Empty -> processEmptyState(controller)
                state is PlayerState.Stopped && controller.isPlaying -> controller.pause()
                state is PlayerState.Playing && !controller.isPlaying -> {
                    currentAudioItem
                        .takeIf { controller.currentMediaItem == null }
                        ?.let { processNewAudioItem(it) }

                    controller.playWhenReady = true
                }
            }
        }
    }

    private fun processEmptyState(controller: MediaController) {
        Timber.d("-> processEmptyState: ")
        controller.stop()
        controller.clearMediaItems()
    }

    override fun onStop() {
        Timber.d("-> onStop: ")
        if (mediaController?.isPlaying == true) return
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}