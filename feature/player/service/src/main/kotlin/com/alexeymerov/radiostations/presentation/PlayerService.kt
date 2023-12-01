package com.alexeymerov.radiostations.presentation

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase.PlayerState
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
    lateinit var ioScope: CoroutineScope

    @Inject
    lateinit var audioUseCase: AudioUseCase

    private var mediaLibrarySession: MediaLibrarySession? = null

    private var callback: MediaLibrarySession.Callback = object : MediaLibrarySession.Callback {

        @UnstableApi
        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            val deferred = ioScope.async {
                val settable = SettableFuture.create<MediaItemsWithStartPosition>()
                val item = audioUseCase.getLastPlayingMediaItem().first()
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
        val player = ExoPlayer.Builder(this).build()

        player.onStateChange(
            onIsPlaying = { isPlaying ->
                Timber.d("onIsPlaying $isPlaying")
                ioScope.launch {
                    val state = if (isPlaying) PlayerState.PLAYING else PlayerState.STOPPED
                    audioUseCase.updatePlayerState(state)
                }
            },
            onIsLoading = { isLoading ->
                Timber.d("onIsLoading $isLoading")
                ioScope.launch {
                    val state = if (isLoading) PlayerState.LOADING else PlayerState.PLAYING
                    audioUseCase.updatePlayerState(state)
                }
            }
        )

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = mediaLibrarySession

    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaLibrarySession?.run {
            if (!player.playWhenReady || player.mediaItemCount == 0) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
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
        addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (!isLoading) onIsPlaying.invoke(isPlaying)
            }

            /**
             * onIsLoadingChanged not working as expected
             * */
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Timber.d("onPlaybackStateChanged $playbackState")

                when {
                    playbackState == STATE_READY -> onIsLoading.invoke(false)
                    playbackState == STATE_BUFFERING || isLoading -> onIsLoading.invoke(true)
                }
            }
        })
    }
}