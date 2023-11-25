package com.alexeymerov.radiostations.presentation

import android.content.ComponentName
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
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.common.collectWhenStarted
import com.alexeymerov.radiostations.presentation.common.mapToMediaItem
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
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

        viewModel.currentAudioItem.filterNotNull().collectWhenStarted(this, ::processCurrentAudioItem)
        viewModel.playerState.collectWhenStarted(this, ::processPlayerState)

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

    private fun setupPlayer() {
        val sessionToken = SessionToken(this, ComponentName(this, PlayerService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.let { future ->
            val listener: () -> Unit = { mediaController = future.get() }
            future.addListener(listener, MoreExecutors.directExecutor())
        }
    }

    private fun processCurrentAudioItem(item: AudioItemDto) {
        Timber.d("activity currentMediaItem $item")
        mediaController?.also { controller ->
            if (item.directUrl != controller.currentMediaItem?.mediaId) {
                controller.setMediaItem(mapToMediaItem(item))
                controller.prepare()
            }
        }
    }

    private fun processPlayerState(it: PlayerState) {
        Timber.d("activity playerState $it")
        mediaController?.also { controller ->
            when (it) {
                PlayerState.EMPTY -> {
                    controller.stop()
                    controller.clearMediaItems()
                }

                PlayerState.PLAYING -> {
                    if (!controller.isPlaying) {
                        controller.playWhenReady = true
                    }
                }

                PlayerState.STOPPED -> controller.pause()
                PlayerState.BUFFERING -> {
                    // nothing at the moment but todo
                }
            }
        }
    }

    override fun onStop() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        super.onStop()
    }

}