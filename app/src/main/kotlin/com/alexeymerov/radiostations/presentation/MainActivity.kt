package com.alexeymerov.radiostations.presentation

import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.extensions.collectWhenStarted
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.feature.player.service.PlayerService
import com.alexeymerov.radiostations.feature.player.service.mapToMediaItem
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var analytics: FirebaseAnalytics

    private val viewModel: MainViewModel by viewModels()

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var starDest: Tabs = Tabs.Browse
        intent.getStringExtra(FAV_SHORTCUT_INTENT_NAME)?.let {
            if (it == FAV_SHORTCUT_ID) starDest = Tabs.Favorites
        }

        val goToRoute: String? = intent.getStringExtra(INTENT_KEY_URL)?.let {
            Screens.Player(starDest.route).createRoute(it)
        }

        val config = Configuration(resources.configuration)
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
            param("start_tab", starDest.route)
            param("is_andscape", config.isLandscape().toString())
            param("screen_size", "${config.screenWidthDp} x ${config.screenHeightDp}")
        }

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

                MainNavGraph(
                    starDest = starDest,
                    goToRoute = goToRoute,
                    playerState = playerState,
                    currentMedia = currentMedia,
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
            val listener = { mediaController = future.get() }
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

        val shortLabel = item.title.substring(0, 10) + "..." // todo --------------------
        val longLabel = "${item.title} (${item.subTitle})"

        val shortcut = ShortcutInfoCompat.Builder(this, DYNAMIC_SHORTCUT_ID)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setDisabledMessage("Not")
            .setIcon(IconCompat.createWithResource(this, com.alexeymerov.radiostations.core.ui.R.drawable.icon_radio))
            .setIntent(
                Intent(this, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    val bundle = Bundle()
                    bundle.putString(INTENT_KEY_URL, item.parentUrl)
                    putExtras(bundle)
                }
            )
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)
    }

    private fun processPlayerState(it: PlayerState) {
        Timber.d("activity playerState $it" + " == currentMediaItem ${mediaController?.currentMediaItem}")
        mediaController?.also { controller ->
            when (it) {
                PlayerState.EMPTY -> {
                    controller.stop()
                    controller.clearMediaItems()
                    ShortcutManagerCompat.disableShortcuts(
                        /* context = */ this,
                        /* shortcutIds = */ listOf(DYNAMIC_SHORTCUT_ID),
                        /* disabledMessage = */ "Not available" // kinda wierd we can't remove pinned icon
                    )
                    ShortcutManagerCompat.removeAllDynamicShortcuts(this)
                }

                PlayerState.PLAYING -> {
                    if (!controller.isPlaying) {
                        if (controller.currentMediaItem == null) {
                            viewModel.currentAudioItem.value?.let { item ->
                                processCurrentAudioItem(item)
                            }
                        }

                        controller.playWhenReady = true
                    }
                }

                PlayerState.STOPPED -> controller.pause()
                PlayerState.LOADING -> { /* no action needed */
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

    companion object {
        private const val FAV_SHORTCUT_INTENT_NAME = "screen"
        private const val FAV_SHORTCUT_ID = "favorites"
        private const val DYNAMIC_SHORTCUT_ID = "latest_station_static_id"
        private const val INTENT_KEY_URL = "parent_url"
    }

}