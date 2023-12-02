package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.presentation.MainViewModel

val LocalNavController = compositionLocalOf<NavHostController> { error("NavHostController not found") }

@Composable
fun MainNavGraph(
    playerState: PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val navController = rememberNavController()
    var topBarState by rememberSaveable { mutableStateOf(TopBarState(String.EMPTY)) }
    val topBarBlock: (TopBarState) -> Unit = { topBarState = it }

    CompositionLocalProvider(LocalNavController provides navController) {
        Surface {
            Scaffold(
                topBar = { TopBar(topBarState) },
                bottomBar = { BottomBar(playerState, playerTitle, onPlayerAction) },
                content = { paddingValues -> CreateScaffoldContent(paddingValues, topBarBlock) }
            )
        }
    }
}

@Composable
private fun CreateScaffoldContent(
    paddingValues: PaddingValues,
    topBarBlock: (TopBarState) -> Unit
) {
    Surface(Modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = LocalNavController.current,
            startDestination = Tabs.Browse.route,
            enterTransition = {
                slideIntoContainer(SlideDirection.Left, spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn()
            },
            exitTransition = {
                slideOutOfContainer(SlideDirection.Left, spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            },
            popEnterTransition = {
                slideIntoContainer(SlideDirection.Right, spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn()
            },
            popExitTransition = {
                slideOutOfContainer(SlideDirection.Right, spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
            }
        ) {
            browseGraph(topBarBlock)
            favoriteGraph(topBarBlock)
            youGraph(topBarBlock)
        }
    }
}