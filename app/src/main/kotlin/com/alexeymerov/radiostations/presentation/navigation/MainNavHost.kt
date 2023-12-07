package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isPortrait
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.MainViewModel

val LocalNavController = compositionLocalOf<NavHostController> { error("NavHostController not found") }

@Composable
fun MainNavGraph(
    starDest: Tabs,
    playerState: PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val navController = rememberNavController()
    var topBarState by rememberSaveable { mutableStateOf(TopBarState(String.EMPTY)) }
    val topBarBlock: (TopBarState) -> Unit = { topBarState = it }
    val configuration = LocalConfiguration.current

    CompositionLocalProvider(LocalNavController provides navController) {
        Surface {
            Scaffold(
                topBar = { TopBar(navController, topBarState) },
                bottomBar = { if (configuration.isPortrait()) BottomBarWithPlayer(navController, playerState, playerTitle, onPlayerAction) },
                content = { paddingValues ->
                    Surface(Modifier.fillMaxSize()) {
                        if (configuration.isLandscape()) {
                            Row(Modifier.fillMaxSize()) {
                                CreateNavigationRail(navController)
                                CreateNavHost(
                                    starDest = starDest,
                                    paddingValues = paddingValues,
                                    topBarBlock = topBarBlock,
                                )
                            }
                        } else {
                            CreateNavHost(
                                starDest = starDest,
                                paddingValues = paddingValues,
                                topBarBlock = topBarBlock,
                            )
                        }

                    }
                }
            )
        }
    }
}

@Composable
private fun CreateNavHost(
    starDest: Tabs = Tabs.Browse,
    paddingValues: PaddingValues,
    topBarBlock: (TopBarState) -> Unit,
) {
    val navController = LocalNavController.current
    NavHost(
        modifier = Modifier.padding(paddingValues),
        navController = navController,
        startDestination = starDest.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
    ) {
        browseGraph(topBarBlock)
        favoriteGraph(topBarBlock)
        youGraph(topBarBlock)
    }
}