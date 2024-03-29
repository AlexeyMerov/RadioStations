package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.common.LocalPlayerVisibility
import com.alexeymerov.radiostations.core.ui.common.LocalSnackbar
import com.alexeymerov.radiostations.core.ui.common.LocalTopbar
import com.alexeymerov.radiostations.core.ui.common.TopBarState
import com.alexeymerov.radiostations.core.ui.extensions.graphicsScale
import com.alexeymerov.radiostations.core.ui.extensions.isLandscape
import com.alexeymerov.radiostations.core.ui.extensions.isPortrait
import com.alexeymerov.radiostations.core.ui.extensions.lerp
import com.alexeymerov.radiostations.core.ui.extensions.setIf
import com.alexeymerov.radiostations.core.ui.extensions.toPx
import com.alexeymerov.radiostations.core.ui.navigation.Tabs
import com.alexeymerov.radiostations.feature.player.screen.exapandable.ExpandableBottomPlayer
import com.alexeymerov.radiostations.presentation.MainViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("NavHostController not found") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    playerState: PlayerState,
    currentMedia: AudioItemDto?,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val config = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    var topBarState by rememberSaveable { mutableStateOf(TopBarState(String.EMPTY)) }
    val topBarBlock: (TopBarState) -> Unit = { topBarState = it }
    val toBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, _, _ ->
            toBarScrollBehavior.state.heightOffset = 0f
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    // sheetState works incorrect. After sheetState.hide() sheetState.isVisible can ba true on some devices
    val isPlayerVisible by remember(playerState) {
        derivedStateOf { playerState !is PlayerState.Empty }
    }

    val bottomSheetState = rememberStandardBottomSheetState(
        skipHiddenState = false,
        initialValue = if (isPlayerVisible) SheetValue.PartiallyExpanded else SheetValue.Hidden
    )

    val sheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackbar provides snackbarHostState,
        LocalPlayerVisibility provides isPlayerVisible,
        LocalTopbar provides topBarBlock
    ) {
        Surface {
            val peekHeightDp = 46.dp
            val peekHeightPx = peekHeightDp.toPx()

            /**
             * This one is wierd. Can't find is it me or some bug.
             * I hope it's a temp workaround.
             * Problem: Only on some SDK. It will automatically change state yo PartiallyExpanded after Hidden
             * Even though "initialValue = SheetValue.Hidden".
             * confirmValueChange also not triggering for some reason.
             * */
            LaunchedEffect(bottomSheetState.targetValue) {
                Timber.d("MainNavGraph - playerSheetState targetValue ${bottomSheetState.targetValue}")
                if (bottomSheetState.targetValue == SheetValue.PartiallyExpanded
                    && (currentMedia == null || playerState is PlayerState.Empty)
                ) {
                    bottomSheetState.hide()
                }
            }

            val railBarSize = when {
                config.isLandscape() -> 80.dp.toPx()
                else -> 80.dp.toPx() + WindowInsets.navigationBars.getBottom(LocalDensity.current).toFloat()
            }

            var sheetFullHeightPx by remember { mutableFloatStateOf(0f) }

            val sheetOffset by remember(bottomSheetState) {
                derivedStateOf {
                    runCatching { bottomSheetState.requireOffset() }.getOrDefault(0f)
                }
            }

            val progress by remember(sheetOffset, sheetFullHeightPx) {
                derivedStateOf {
                    val maxOffset = sheetFullHeightPx - railBarSize - peekHeightPx
                    ((maxOffset - sheetOffset) / maxOffset).coerceIn(0f, 1f)
                }
            }

            val collapsedColor = MaterialTheme.colorScheme.primary
            val expandedColor = MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation)
            val onCollapsedColor = MaterialTheme.colorScheme.onPrimary
            val onExpandedColor = MaterialTheme.colorScheme.onSurface

            val animData by remember(progress) {
                derivedStateOf {
                    calculateAnimData(
                        progress = progress,
                        railBarSize = railBarSize,
                        collapsedColor = collapsedColor,
                        expandedColor = expandedColor,
                        onCollapsedColor = onCollapsedColor,
                        onExpandedColor = onExpandedColor
                    )
                }
            }

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(toBarScrollBehavior.nestedScrollConnection)
                    .setIf(config.isLandscape()) { displayCutoutPadding() },
                bottomBar = {
                    if (config.isPortrait()) {
                        CreateBottomBar(
                            modifier = Modifier.graphicsLayer { translationY = animData.bottomBarOffsetY }
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        modifier = Modifier.padding(bottom = if (isPlayerVisible) peekHeightDp else 0.dp),
                        hostState = snackbarHostState
                    )
                },
                content = { scaffoldPaddingValues ->
                    Timber.d("MainNavGraph - scaffoldPaddingValues: $scaffoldPaddingValues")
                    BottomSheetScaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                // top is already handled by the topBar
                                bottom = scaffoldPaddingValues.calculateBottomPadding(),
                                end = scaffoldPaddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                start = scaffoldPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            ),
                        scaffoldState = sheetScaffoldState,
                        topBar = { TopBar(topBarState, toBarScrollBehavior) },
                        sheetDragHandle = null,
                        sheetPeekHeight = peekHeightDp + scaffoldPaddingValues.calculateBottomPadding(),
                        sheetShape = RoundedCornerShape(topStart = animData.shapeCornerRadius, topEnd = animData.shapeCornerRadius),
                        sheetContent = {
                            ExpandableBottomPlayer(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onGloballyPositioned { sheetFullHeightPx = it.size.height.toFloat() },
                                peekHeightDp = peekHeightDp,
                                progress = progress,
                                containerColor = animData.containerColor,
                                onContainerColor = animData.onContainerColor,
                                playerState = playerState,
                                currentMedia = currentMedia,
                                onCloseAction = {
                                    coroutineScope.launch { bottomSheetState.hide() }
                                    onPlayerAction.invoke(MainViewModel.ViewAction.NukePlayer)
                                },
                                onToggleAudio = {
                                    onPlayerAction.invoke(MainViewModel.ViewAction.ToggleAudio)
                                },
                                onCollapse = {
                                    coroutineScope.launch { bottomSheetState.partialExpand() }
                                }
                            )
                        },
                        content = { sheetContentPadding ->
                            Timber.d("MainNavGraph - sheetContentPadding: $sheetContentPadding")
                            Surface(Modifier.fillMaxSize()) {
                                Row(Modifier.fillMaxSize()) {
                                    if (config.isLandscape()) {
                                        CreateNavigationRail(
                                            modifier = Modifier.graphicsLayer { translationX = animData.railBarOffsetX }
                                        )
                                    }
                                    CreateNavHost(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsScale(animData.scaleContent),
                                    )
                                }
                            }
                        }
                    )
                }
            )

            LaunchedEffect(playerState) {
                when {
                    playerState is PlayerState.Empty && bottomSheetState.isVisible -> bottomSheetState.hide()
                    playerState !is PlayerState.Empty && !bottomSheetState.isVisible -> bottomSheetState.partialExpand()
                }
            }
        }
    }
}

@Composable
private fun CreateNavHost(
    modifier: Modifier = Modifier,
    starDest: Tabs = Tabs.Browse,
) {
    val navController = LocalNavController.current
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = starDest.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
    ) {
        browseGraph()
        favoriteGraph()
        youGraph()
    }
}

private fun calculateAnimData(
    progress: Float,
    railBarSize: Float,
    collapsedColor: Color,
    expandedColor: Color,
    onCollapsedColor: Color,
    onExpandedColor: Color,
): CollapseExpandData {

    val bottomBarOffsetY = lerp(0f, railBarSize, progress)
    val railBarOffsetX = lerp(0f, -railBarSize, progress)
    val scaleContent = lerp(1f, 0f, progress)
    val containerColor = collapsedColor.lerp(expandedColor, progress)
    val onContainerColor = onCollapsedColor.lerp(onExpandedColor, progress)
    val shapeCornerRadius = androidx.compose.ui.unit.lerp(12.dp, 0.dp, progress)

    return CollapseExpandData(
        bottomBarOffsetY = bottomBarOffsetY,
        railBarOffsetX = railBarOffsetX,
        scaleContent = scaleContent,
        containerColor = containerColor,
        onContainerColor = onContainerColor,
        shapeCornerRadius = shapeCornerRadius
    )
}

private data class CollapseExpandData(
    val bottomBarOffsetY: Float,
    val railBarOffsetX: Float,
    val scaleContent: Float,
    val containerColor: Color,
    val onContainerColor: Color,
    val shapeCornerRadius: Dp
)