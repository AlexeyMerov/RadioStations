package com.alexeymerov.radiostations.feature.player.service

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import com.alexeymerov.radiostations.core.common.di.AppScope
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.feature.player.common.mapToMediaItem
import com.alexeymerov.radiostations.feature.player.widget.PlayerWidgetReceiver
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.asListenableFuture
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaLibraryService() {

    @Inject
    @AppScope
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var playingUseCase: PlayingUseCase

    private var mediaLibrarySession: MediaLibrarySession? = null

    private var callback: MediaLibrarySession.Callback = object : MediaLibrarySession.Callback {

        @UnstableApi
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            Timber.d("-> onPlaybackResumption: ")

            val deferred = coroutineScope.async {
                val settable = SettableFuture.create<MediaItemsWithStartPosition>()
                val item = playingUseCase.getLastPlayingMediaItem().first()
                if (item != null) {
                    val mediaItemsWithStartPosition = MediaItemsWithStartPosition(
                        /* mediaItems = */ listOf(mapToMediaItem(item)),
                        /* startIndex = */ 0,
                        /* startPositionMs = */ 0
                    )
                    settable.set(mediaItemsWithStartPosition)
                }

                return@async settable
            }

            return deferred.asListenableFuture().get()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("-> onCreate: ")

        val player = ExoPlayer.Builder(this).build()
        player.onStateChange(
            onIsPlaying = { isPlaying ->
                Timber.d("--> onIsPlaying $isPlaying")
                coroutineScope.launch {
                    val state = if (isPlaying) PlayerState.Playing() else PlayerState.Stopped()
                    playingUseCase.updatePlayerState(state)
                    updateWidget()
                }
            },
            onIsLoading = { isLoading ->
                Timber.d("--> onIsLoading")
                coroutineScope.launch {
                    val playerState = playingUseCase.getPlayerState().first()
                    when {
                        isLoading -> playingUseCase.updatePlayerState(PlayerState.Loading)
                        playerState is PlayerState.Loading -> playingUseCase.updatePlayerState(PlayerState.Playing())
                    }
                }
            }
        )

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback).build()
    }

    private fun updateWidget() {
        Timber.d("-> updateWidget: ")
        sendBroadcast(
            Intent(this, PlayerWidgetReceiver::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        Timber.d("-> onGetSession: $mediaLibrarySession")
        return mediaLibrarySession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.d("-> onTaskRemoved: ")
        mediaLibrarySession?.run {
            if (!player.isPlaying) release()
        }
    }

    override fun onDestroy() {
        Timber.d("-> onDestroy: ")
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }

    private fun Player.onStateChange(
        onIsPlaying: (Boolean) -> Unit = {},
        onIsLoading: (Boolean) -> Unit = {}
    ) {
        Timber.d("-> onStateChange: ")
        addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                Timber.d("PlayerService -- onIsPlayingChanged $isPlaying")
                if (playbackState != STATE_BUFFERING) onIsPlaying.invoke(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Timber.d("PlayerService -- onPlaybackStateChanged $playbackState")
                onIsLoading.invoke(playbackState == STATE_BUFFERING)
            }
        })
    }
}