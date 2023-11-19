package com.alexeymerov.radiostations.presentation

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.common.collectWhenStarted
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var viewState by mutableStateOf(viewModel.initialState)

        splashScreen.setKeepOnScreenCondition { viewState == ViewState.Loading }

        viewModel.viewState.collectWhenStarted(this) { viewState = it }

        enableEdgeToEdge()
        setContent {
            val themeState = when (val state = viewState) {
                is ViewState.Loaded -> state.themeState
                else -> return@setContent // not sure about the best practice
            }

            StationsAppTheme(themeState) {
                val playerState by viewModel.playerState.collectAsStateWithLifecycle()
                val currentMedia by viewModel.currentAudioItem.collectAsStateWithLifecycle()
                val playerTitle by remember { derivedStateOf { currentMedia?.title ?: String.EMPTY } }
                MainNavGraph(
                    playerState = playerState,
                    playerTitle = playerTitle,
                    onPlayerAction = { viewModel.setAction(it) }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setupPlayer()
    }

    // todo remove hidden media notification under control buttons. currently resume playback will work only from usual notification
    // todo add buffering state
    private fun setupPlayer() {
        val sessionToken = SessionToken(this, ComponentName(this, PlayerService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.let { future ->
            val listener: () -> Unit = {
                mediaController = future.get()
                mediaController?.let { controller ->
                    controller.onIsPlaying { isPlaying ->
                        val action = if (isPlaying) MainViewModel.ViewAction.PlayAudio else MainViewModel.ViewAction.StopAudio
                        viewModel.setAction(action)
                    }

                    viewModel.currentAudioItem.collectWhenStarted(this) { item ->
                        processCurrentAudioItem(item, controller)
                    }

                    viewModel.getPlayerState(controller.isPlaying).collectWhenStarted(this) {
                        processPlayerState(it, controller)
                    }
                }
            }

            future.addListener(listener, MoreExecutors.directExecutor())
        }
    }

    private fun MediaController.onIsPlaying(action: (Boolean) -> Unit) {
        addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                action.invoke(isPlaying)
            }
        })
    }

    private fun processCurrentAudioItem(item: AudioItemDto?, controller: MediaController) {
        Timber.d("activity currentMediaItem $item")

        if (item != null && item.directUrl != controller.currentMediaItem?.mediaId) {
            val mediaMetadata = MediaMetadata.Builder()
                .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
                .setTitle(item.title)
                .setArtist(item.subTitle)
                .setArtworkUri(Uri.parse(item.image))
                .build()

            val mediaItem = MediaItem.Builder()
                .setMediaId(item.directUrl)
                .setUri(item.directUrl)
                .setMediaMetadata(mediaMetadata)
                .build()

            controller.setMediaItem(mediaItem)
            controller.prepare()
        }
    }

    private fun processPlayerState(it: MainViewModel.PlayerState, controller: MediaController) {
        when (it) {
            is MainViewModel.PlayerState.Empty -> {
                controller.stop()
                controller.clearMediaItems()
            }

            is MainViewModel.PlayerState.Playing -> {
                if (!controller.isPlaying) {
                    controller.playWhenReady = true
                }
            }

            is MainViewModel.PlayerState.Stopped -> controller.pause()
        }
    }

    override fun onStop() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        super.onStop()
    }

}