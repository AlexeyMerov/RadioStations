package com.alexeymerov.radiostations.feature.player.manager

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.core.common.di.AppScope
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.feature.player.common.mapToMediaItem
import com.alexeymerov.radiostations.feature.player.service.PlayerService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MediaManagerImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val playingUseCase: PlayingUseCase,
    @AppScope private val coroutineScope: CoroutineScope,
) : MediaManager {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var currentAudioItem: AudioItemDto? = null
    private var listeneres = mutableListOf<MediaManager.Listener>()

    override fun setupPlayer() {
        Timber.d("-> setupPlayer: $mediaController")
        if (mediaController != null) {
            notifyListeners()
            return
        }

        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.let { future ->
            val listener = {
                mediaController = future.get()
                subscribeToEvents()
            }
            future.addListener(listener, MoreExecutors.directExecutor())
        }
    }

    private fun subscribeToEvents() {
        Timber.d("-> subscribeToEvents: ")
        coroutineScope.launch {
            playingUseCase.getPlayerState().collectLatest {
                withContext(Dispatchers.Main) {
                    processPlayerState(it)
                }
            }
        }

        coroutineScope.launch {
            playingUseCase.getLastPlayingMediaItem().collectLatest {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        mediaController?.processEmptyState()
                    } else {
                        processNewAudioItem(it)
                    }
                }
            }
        }

        notifyListeners()
    }

    private fun notifyListeners() {
        Timber.d("-> notifyListeners: ")
        listeneres.forEach { it.onControllerInitialized() }
    }

    private fun processNewAudioItem(item: AudioItemDto) {
        Timber.d("processCurrentAudioItem $item")
        currentAudioItem = item

        mediaController?.also { controller ->
            if (item.directUrl != controller.currentMediaItem?.mediaId) {
                controller.setMediaItem(mapToMediaItem(item))
                controller.prepare()
            }
        }
    }

    private fun processPlayerState(state: PlayerState) {
        Timber.d("processPlayerState - playerState $state ## mediaController $mediaController ## currentMediaItem (${mediaController?.mediaItemCount}) ${mediaController?.currentMediaItem}")
        mediaController?.also { controller ->
            when {
                state is PlayerState.Empty -> controller.processEmptyState()
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

    override fun play() {
        Timber.d("-> play: ")
        coroutineScope.launch {
            playingUseCase.updatePlayerState(PlayerState.Playing(isUserAction = true))
        }
    }

    override fun stop() {
        Timber.d("-> stop: ")
        coroutineScope.launch {
            playingUseCase.updatePlayerState(PlayerState.Stopped(isUserAction = true))
        }
    }

    override fun addListener(listener: MediaManager.Listener) {
        listeneres.add(listener)
    }

    private fun MediaController.processEmptyState() {
        Timber.d("-> processEmptyState: ")
        stop()
        clearMediaItems()
    }

    override fun onDestroy() {
        Timber.d("-> onStop: ")
        if (mediaController?.isPlaying == true) return
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}