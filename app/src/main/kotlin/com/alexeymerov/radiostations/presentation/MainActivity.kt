package com.alexeymerov.radiostations.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
import com.alexeymerov.radiostations.core.common.ThemeState
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.extensions.collectWhenCreated
import com.alexeymerov.radiostations.core.ui.extensions.collectWhenStarted
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.feature.player.manager.MediaServiceManager
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.navigation.Route
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var mediaServiceManager: MediaServiceManager

    private val viewModel: MainViewModel by viewModels()

    private var viewState by mutableStateOf<ViewState>(ViewState.Loading)

    private var currentMedia by mutableStateOf<AudioItemDto?>(null)

    private var playerState by mutableStateOf(PlayerState.EMPTY)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { viewState == ViewState.Loading }
        super.onCreate(savedInstanceState)

        subscribeToEvents()
        mediaServiceManager.setupPlayer()

        val (starDest, goToRoute) = prepareNavigation()
        sendAnalyticEvents(starDest)

        enableEdgeToEdge()
        setContent {
            val themeState = remember(viewState) {
                when (val state = viewState) {
                    is ViewState.Loaded -> state.themeState
                    else -> ThemeState()
                }
            }

            StationsAppTheme(themeState) {
                val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()

                MainNavGraph(
                    starDest = starDest,
                    goToRoute = goToRoute,
                    playerState = playerState,
                    currentMedia = currentMedia,
                    isNetworkAvailable = isNetworkAvailable,
                    onPlayerAction = { viewModel.setAction(it) }
                )
            }
        }
    }

    private fun subscribeToEvents() {
        viewModel.viewState.collectWhenCreated(this) { viewState = it }

        viewModel.currentAudioItem.collectWhenStarted(this) {
            currentMedia = it
            if (it != null) mediaServiceManager.processCurrentAudioItem(it)
        }

        viewModel.playerState.collectWhenStarted(this) {
            playerState = it
            mediaServiceManager.processPlayerState(it, viewModel.currentAudioItem.value)
        }
    }

    private fun sendAnalyticEvents(starDest: Tabs) {
        val config = Configuration(resources.configuration)
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
            param(AnalyticsParams.START_TAB, starDest.route)
            param(AnalyticsParams.IS_LANDSCAPE, config.isLandscape().toString())
            param(AnalyticsParams.SCREEN_SIZE, "${config.screenWidthDp} x ${config.screenHeightDp}")
        }
    }

    private fun prepareNavigation(): Pair<Tabs, Route> {
        var starDest: Tabs = Tabs.Browse
        intent.getStringExtra(FAV_SHORTCUT_INTENT_NAME)?.let {
            if (it == FAV_SHORTCUT_ID) starDest = Tabs.Favorites
        }

        val goToRoute = mediaServiceManager.getStationRouteIfExist(intent)

        return Pair(starDest, Route(goToRoute))
    }

    override fun onDestroy() {
        mediaServiceManager.onStop()
        super.onDestroy()
    }

    companion object {
        private const val FAV_SHORTCUT_INTENT_NAME = "screen"
        private const val FAV_SHORTCUT_ID = "favorites"
    }

}