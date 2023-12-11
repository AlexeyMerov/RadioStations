package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isPortrait
import com.alexeymerov.radiostations.core.ui.extensions.toPx
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.MainViewModel
import kotlinx.coroutines.launch

val LocalNavController = compositionLocalOf<NavHostController> { error("NavHostController not found") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    starDest: Tabs,
    playerState: PlayerState,
    currentMedia: AudioItemDto?,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val navController = rememberNavController()
    var topBarState by rememberSaveable { mutableStateOf(TopBarState(String.EMPTY)) }
    val topBarBlock: (TopBarState) -> Unit = { topBarState = it }

    val config = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalNavController provides navController) {
        Surface {
            val peekHightDp = 46.dp
            val peekHightPx = peekHightDp.toPx()

            val sheetState = rememberStandardBottomSheetState(
                skipHiddenState = false,
                initialValue = SheetValue.Hidden
            )

            val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

            var bottomPaddingPx by remember { mutableFloatStateOf(0f) }
            var sheetFullHeightPx by remember { mutableFloatStateOf(0f) }

            val sheetOffset by remember(sheetState) {
                derivedStateOf {
                    runCatching { sheetState.requireOffset() }.getOrDefault(0f)
                }
            }

            val progress by remember(sheetOffset, sheetFullHeightPx, bottomPaddingPx) {
                derivedStateOf {
                    val maxOffset = sheetFullHeightPx - bottomPaddingPx - peekHightPx
                    ((maxOffset - sheetOffset) / maxOffset).coerceIn(0f, 1f)
                }
            }

            val bottomBarYOffset by remember(progress) {
                derivedStateOf {
                    lerp(0f, bottomPaddingPx, progress)
                }
            }

            Scaffold(
                bottomBar = {
                    if (config.isPortrait()) {
                        CreateBottomBar(
                            modifier = Modifier.graphicsLayer {
                                translationY = bottomBarYOffset
                            },
                            navController = navController
                        )
                    }
                },
                content = { scaffoldPaddingValues ->
                    bottomPaddingPx = scaffoldPaddingValues.calculateBottomPadding().toPx()

                    BottomSheetScaffold(
                        modifier = Modifier,
                        scaffoldState = scaffoldState,
                        topBar = { TopBar(navController, topBarState) },
                        sheetDragHandle = null,
                        sheetPeekHeight = peekHightDp + scaffoldPaddingValues.calculateBottomPadding(),
                        sheetShape = RectangleShape,
                        sheetContent = {
                            ExpandableBottomPlayer(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { sheetFullHeightPx = it.size.height.toFloat() },
                                peekHightDp = peekHightDp,
                                progress = progress,
                                containerColor = MaterialTheme.colorScheme.primary,
                                onContainerColor = MaterialTheme.colorScheme.onPrimary,
                                playerState = playerState,
                                currentMedia = currentMedia,
                                onPlayerAction = {
                                    if (it is MainViewModel.ViewAction.NukePlayer) {
                                        coroutineScope.launch {
                                            sheetState.hide()
                                        }
                                    }
                                    onPlayerAction.invoke(it)
                                },
                                onCollapse = {
                                    coroutineScope.launch {
                                        sheetState.partialExpand()
                                    }
                                }
                            )
                        },
                        content = { sheetContentPadding ->
                            Surface(Modifier.fillMaxSize()) {
                                if (config.isLandscape()) {
                                    Row(Modifier.fillMaxSize()) {
                                        CreateNavigationRail(navController)
                                        CreateNavHost(
                                            starDest = starDest,
                                            paddingValues = sheetContentPadding,
                                            topBarBlock = topBarBlock,
                                        )
                                    }
                                } else {
                                    CreateNavHost(
                                        modifier = Modifier.alpha(1f - progress),
                                        starDest = starDest,
                                        paddingValues = sheetContentPadding,
                                        topBarBlock = topBarBlock,
                                    )
                                }

                            }
                        }
                    )
                }
            )

            LaunchedEffect(playerState) {
                when {
                    playerState == PlayerState.EMPTY && sheetState.isVisible -> sheetState.hide()
                    playerState != PlayerState.EMPTY && !sheetState.isVisible -> sheetState.partialExpand()
                }
            }
        }
    }
}

@Composable
private fun CreateNavHost(
    modifier: Modifier = Modifier,
    starDest: Tabs = Tabs.Browse,
    paddingValues: PaddingValues,
    topBarBlock: (TopBarState) -> Unit,
) {
    val navController = LocalNavController.current
    NavHost(
        modifier = modifier
            .height(400.dp)
            .padding(paddingValues),
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