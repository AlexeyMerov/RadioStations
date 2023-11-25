package com.alexeymerov.radiostations.presentation.navigation

import android.os.Parcelable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase.*
import com.alexeymerov.radiostations.presentation.MainViewModel
import com.alexeymerov.radiostations.presentation.common.view.BasicText
import com.alexeymerov.radiostations.presentation.common.view.DropDownRow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Composable
fun MainNavGraph(
    playerState: PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val navController = rememberNavController()
    var topBarState by rememberSaveable { mutableStateOf(TopBarState(String.EMPTY)) }
    val topBarBlock: (TopBarState) -> Unit = { topBarState = it }

    Surface {
        Scaffold(
            topBar = { CreateTopBar(topBarState, navController) },
            bottomBar = { CreateBottomBar(navController, playerState, playerTitle, onPlayerAction) },
            content = { paddingValues -> CreateScaffoldContent(navController, paddingValues, topBarBlock) }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CreateTopBar(barState: TopBarState, navController: NavController) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent), //with color it has some delay for color animation
        title = { TopBarTitle(barState.title, barState.subTitle) },
        navigationIcon = {
            if (barState.selectedItems == 0) {
                NavigationIcon(barState.displayBackButton, navController)
            }
        },
        actions = { TopBarActions(barState.rightIcon) }
    )
}

@Composable
private fun TopBarTitle(title: String, subTitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = title,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = String.EMPTY
        ) { targetText ->
            Text(text = targetText, fontWeight = FontWeight.Bold)
        }
        if (subTitle.isNotEmpty()) {
            AnimatedContent(
                targetState = subTitle,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = String.EMPTY
            ) { targetText ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .alpha(0.7f)
                            .size(12.dp),
                        imageVector = Icons.Outlined.LocationCity,
                        contentDescription = String.EMPTY
                    )

                    Text(
                        modifier = Modifier
                            .alpha(0.7f)
                            .padding(start = 4.dp),
                        text = targetText,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationIcon(displayBackButton: Boolean, navController: NavController) {
    AnimatedVisibility(
        visible = displayBackButton,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
    }
}

@Composable
private fun RowScope.TopBarActions(
    rightIcon: RightIconItem?
) {
    AnimatedVisibility(
        visible = rightIcon != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        var needShowMenu by rememberSaveable { mutableStateOf(false) }
        rightIcon?.let {
            IconButton(
                onClick = {
                    if (it.dropDownMenu != null) {
                        needShowMenu = !needShowMenu
                    }
                    it.action.invoke()
                }
            ) {
                it.icon.iconResId?.let { iconRes ->
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = String.EMPTY
                    )
                } ?: Icon(
                    imageVector = it.icon.iconVector,
                    contentDescription = String.EMPTY
                )
            }

            DropdownMenu(
                expanded = needShowMenu,
                onDismissRequest = { needShowMenu = false },
                modifier = Modifier.defaultMinSize(minWidth = 125.dp),
                offset = DpOffset(x = 12.dp, y = 0.dp),
            ) {
                it.dropDownMenu?.forEach {
                    DropDownRow(
                        iconId = it.iconId,
                        stringId = it.stringId,
                        action = it.action
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateBottomBar(
    navController: NavHostController,
    playerState: PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    val tabs = listOf(Tabs.Browse, Tabs.Favorites, Tabs.You)

    Column {
        BottomPlayer(playerState, playerTitle, onPlayerAction)

        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            tabs.forEach { tab ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                BottomBarItem(tab, isSelected, navController)
            }
        }
    }

}

@Composable
private fun BottomPlayer(
    playerState: PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.secondary),

        ) {
        val isVisible = playerState != PlayerState.EMPTY
        AnimatedVisibility(visible = isVisible) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isPlaying = playerState == PlayerState.PLAYING

                PlayingWaves(isPlaying)

                BasicText(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f),
                    text = playerTitle,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                PlayButton(
                    isPlaying = isPlaying,
                    onTogglePlay = { onPlayerAction.invoke(MainViewModel.ViewAction.ToggleAudio) }
                )

                IconButton(
                    modifier = Modifier.padding(start = 4.dp),
                    onClick = { onPlayerAction.invoke(MainViewModel.ViewAction.NukePlayer) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        contentDescription = String.EMPTY
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayingWaves(isPlaying: Boolean) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playing))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE,
        isPlaying = isPlaying,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSecondary.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        modifier = Modifier.size(25.dp),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = {
            if (isPlaying) animationStateProgress else 0f
        },
    )
}

@Composable
private fun PlayButton(isPlaying: Boolean, onTogglePlay: () -> Unit) {
    val lotSpeed = if (isPlaying) -2f else 2f
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playstop))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        speed = lotSpeed,
        clipSpec = LottieClipSpec.Progress(max = 0.5f)
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onSecondary.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        modifier = Modifier
            .size(35.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onSecondary,
                    bounded = false,
                    radius = 20.dp
                ),
                onClick = { onTogglePlay.invoke() }
            ),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = { animationStateProgress }
    )
}

@Composable
private fun RowScope.BottomBarItem(
    tab: Tabs,
    isSelected: Boolean,
    navController: NavHostController
) {
    val icon = if (isSelected) tab.selectedIcon else tab.icon
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = stringResource(tab.stringId)) },
        label = { Text(stringResource(tab.stringId)) },
        selected = isSelected,
        onClick = {
            navController.navigate(tab.route) {
                // Pop up to the start destination of the graph
                // to avoid building up a large stack of destinations on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
private fun CreateScaffoldContent(
    navController: NavHostController,
    paddingValues: PaddingValues,
    topBarBlock: (TopBarState) -> Unit
) {
    Surface(Modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
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
            browseGraph(navController, topBarBlock)
            favoriteGraph(navController, topBarBlock)
            youGraph(navController, topBarBlock)
        }
    }
}

@Immutable
@Stable
@Parcelize
data class TopBarState(
    val title: String,
    val subTitle: String = String.EMPTY,
    val displayBackButton: Boolean = false,
    val rightIcon: RightIconItem? = null,
    val selectedItems: Int = 0
) : Parcelable

@Parcelize
data class RightIconItem(val icon: TopBarIcon) : Parcelable {
    @IgnoredOnParcel
    var action: () -> Unit = {}

    @IgnoredOnParcel
    var dropDownMenu: List<DropDownItem>? = null
}

@Parcelize
data class DropDownItem(val iconId: Int, val stringId: Int) : Parcelable {
    @IgnoredOnParcel
    var action: () -> Unit = {}
}

// doesn't look ok and mb whole TopBar state needs another approach
enum class TopBarIcon(val iconVector: ImageVector, val iconResId: Int? = null) {
    SETTINGS(Icons.Rounded.Settings, R.drawable.icon_settings),
    STAR_HALF(Icons.Rounded.StarHalf),
    STAR(Icons.Rounded.Star),
    STAR_OUTLINE(Icons.Rounded.StarOutline),
}