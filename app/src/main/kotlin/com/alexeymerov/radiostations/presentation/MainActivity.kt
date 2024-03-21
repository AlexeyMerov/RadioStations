package com.alexeymerov.radiostations.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexeymerov.radiostations.common.createShortcut
import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
import com.alexeymerov.radiostations.core.common.ProjectConst.DEEP_LINK_TUNE_PATTERN
import com.alexeymerov.radiostations.core.common.ThemeState
import com.alexeymerov.radiostations.core.common.base64ToBitmap
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.LocalAnalytics
import com.alexeymerov.radiostations.core.ui.common.LocalConnectionStatus
import com.alexeymerov.radiostations.core.ui.extensions.collectWhenStarted
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.feature.player.manager.MediaManager
import com.alexeymerov.radiostations.presentation.MainViewModel.ViewState
import com.alexeymerov.radiostations.presentation.navigation.MainNavGraph
import com.alexeymerov.radiostations.presentation.theme.StationsAppTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var mediaServiceManager: MediaManager

    private val viewModel: MainViewModel by viewModels()

    private var viewState by mutableStateOf<ViewState>(ViewState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("-> onCreate: ")
        installSplashScreen().setKeepOnScreenCondition { viewState == ViewState.Loading }
        super.onCreate(savedInstanceState)

        viewModel.viewState.collectWhenStarted(this) { viewState = it }
        mediaServiceManager.setupPlayer()
        sendAnalyticEvents(Tabs.Browse)

        enableEdgeToEdge()
        setContent { MainContent() }
    }

    @Composable
    private fun MainContent() {
        val themeState = remember(viewState) {
            (viewState as? ViewState.Loaded)?.themeState ?: ThemeState()
        }
        val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsStateWithLifecycle()
        val playerState by viewModel.playerState.collectAsStateWithLifecycle()
        val currentMedia by viewModel.currentAudioItem.collectAsStateWithLifecycle()

        LaunchedEffect(playerState) {
            Timber.d("-> playerState $playerState")
            if (playerState is PlayerState.Empty) removeShortcut()
        }

        LaunchedEffect(currentMedia) {
            Timber.d("-> currentMedia: $currentMedia")
            currentMedia?.let { createDynamicShortcut(it) }
        }

        CompositionLocalProvider(
            LocalConnectionStatus provides isNetworkAvailable,
            LocalAnalytics provides analytics
        ) {
            StationsAppTheme(themeState) {
                MainNavGraph(
                    playerState = playerState,
                    currentMedia = currentMedia,
                    onPlayerAction = { viewModel.setAction(it) }
                )
            }
        }
    }

    override fun onDestroy() {
        mediaServiceManager.onDestroy()
        super.onDestroy()
    }

    private fun createDynamicShortcut(item: AudioItemDto) {
        Timber.d("-> createDynamicShortcut: $item")

        val shortLabel = if (item.title.length > 12) "${item.title.substring(0, 10)}..." else item.title
        var longLabel = item.title
        item.subTitle?.let {
            longLabel = "$longLabel (${item.subTitle})"
        }

        createShortcut(
            activityClass = MainActivity::class.java,
            shortcutId = MediaManager.DYNAMIC_SHORTCUT_ID,
            uri = "$DEEP_LINK_TUNE_PATTERN/${item.tuneId}".toUri(),
            shortLabel = shortLabel,
            longLabel = longLabel,
            icon = item.imageBase64?.base64ToBitmap()?.let { IconCompat.createWithAdaptiveBitmap(it) }
                ?: IconCompat.createWithResource(this, R.drawable.icon_radio)
        )
    }

    private fun removeShortcut() {
        ShortcutManagerCompat.disableShortcuts(
            /* context = */ this,
            /* shortcutIds = */ listOf(MediaManager.DYNAMIC_SHORTCUT_ID),
            /* disabledMessage = */ "Not available" // we can't remove pinned icon
        )
        ShortcutManagerCompat.removeAllDynamicShortcuts(this)
    }

    private fun sendAnalyticEvents(starDest: Tabs) {
        val config = Configuration(resources.configuration)
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) {
            param(AnalyticsParams.START_TAB, starDest.route)
            param(AnalyticsParams.IS_LANDSCAPE, config.isLandscape().toString())
            param(AnalyticsParams.SCREEN_SIZE, "${config.screenWidthDp} x ${config.screenHeightDp}")
        }
    }

}