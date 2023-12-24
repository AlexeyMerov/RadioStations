package com.alexeymerov.radiostations.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.core.ui.extensions.collectWhenStarted
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.mediaservice.MediaServiceManager
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var mediaServiceManager: MediaServiceManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var starDest: Tabs = Tabs.Browse
        intent.getStringExtra(FAV_SHORTCUT_INTENT_NAME)?.let {
            if (it == FAV_SHORTCUT_ID) starDest = Tabs.Favorites
        }

        val goToRoute = mediaServiceManager.getStationRouteIfExist(intent)

        val config = Configuration(resources.configuration)
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
            param("start_tab", starDest.route)
            param("is_andscape", config.isLandscape().toString())
            param("screen_size", "${config.screenWidthDp} x ${config.screenHeightDp}")
        }

        var viewState by mutableStateOf(viewModel.initialState)

        splashScreen.setKeepOnScreenCondition { viewState == ViewState.Loading }

        viewModel.viewState.collectWhenStarted(this) { viewState = it }
        viewModel.currentAudioItem.filterNotNull().collectWhenStarted(this) { mediaServiceManager.processCurrentAudioItem(this, it) }
        viewModel.playerState.collectWhenStarted(this) {
            mediaServiceManager.processPlayerState(this, it, viewModel.currentAudioItem.value)
        }

        enableEdgeToEdge()
        setContent {
            val themeState = when (val state = viewState) {
                is ViewState.Loaded -> state.themeState
                else -> return@setContent // not sure about the best practice
            }

            StationsAppTheme(themeState) {
                val playerState by viewModel.playerState.collectAsStateWithLifecycle()
                val currentMedia by viewModel.currentAudioItem.collectAsStateWithLifecycle()
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

    override fun onStart() {
        super.onStart()
        mediaServiceManager.setupPlayer(this)
    }

    override fun onStop() {
        mediaServiceManager.onStop()
        super.onStop()
    }

    companion object {
        private const val FAV_SHORTCUT_INTENT_NAME = "screen"
        private const val FAV_SHORTCUT_ID = "favorites"
    }

}