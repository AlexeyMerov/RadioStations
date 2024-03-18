package com.alexeymerov.radiostations.feature.player.screen

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.common.LocalTopbar
import com.alexeymerov.radiostations.core.ui.common.RightIconItem
import com.alexeymerov.radiostations.core.ui.common.TopBarIcon
import com.alexeymerov.radiostations.core.ui.common.TopBarState
import com.alexeymerov.radiostations.core.ui.extensions.isPortrait
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.core.ui.view.ErrorView
import com.alexeymerov.radiostations.core.ui.view.LoaderView
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ScreenPlayState
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ViewAction
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ViewState
import com.alexeymerov.radiostations.feature.player.screen.elements.PlayButton
import com.alexeymerov.radiostations.feature.player.screen.elements.PlayerArtwork

@Composable
fun BasePlayerScreen(
    viewModel: PlayerViewModel,
    isVisibleToUser: Boolean
) {
    ComposedTimberD("BasePlayerScreen")

    if (isVisibleToUser) {
        TopBarSetup(
            stationName = viewModel.title,
            subTitle = viewModel.subTitle,
            isFavorite = viewModel.isFavorite,
            onAction = { viewModel.setAction(it) }
        )
    }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    PlayerScreen(
        viewState = viewState,
        onAction = { viewModel.setAction(it) }
    )
}

@Composable
internal fun PlayerScreen(
    viewState: ViewState,
    onAction: (ViewAction) -> Unit
) {
    when (viewState) {
        is ViewState.Loading -> LoaderView()
        is ViewState.Error -> ErrorView()
        is ViewState.ReadyToPlay -> {
            MainContentWithOrientation(
                item = viewState.item,
                playState = viewState.playState,
                onToggleAudio = { bitmap ->
                    onAction.invoke(ViewAction.ChangeOrToggleAudio(viewState.item, bitmap))
                }
            )
        }
    }
}

@Composable
private fun TopBarSetup(
    stationName: String,
    subTitle: String?,
    isFavorite: Boolean?,
    onAction: (ViewAction) -> Unit
) {
    val topBar = LocalTopbar.current
    LaunchedEffect(Unit, subTitle, isFavorite) {
        topBar.invoke(
            TopBarState(
                title = stationName,
                subTitle = subTitle,
                displayBackButton = true,
                rightIcon = run {
                    if (isFavorite == null) null
                    else {
                        val icon = if (isFavorite) TopBarIcon.STAR else TopBarIcon.STAR_OUTLINE
                        RightIconItem(icon).apply {
                            action = { onAction.invoke(ViewAction.ToggleFavorite) }
                        }
                    }
                }

            )
        )
    }
}

@Composable
private fun MainContentWithOrientation(
    item: AudioItemDto,
    playState: ScreenPlayState,
    onToggleAudio: (Bitmap?) -> Unit
) {
    val configuration = LocalConfiguration.current
    val themeBackgroundColor = MaterialTheme.colorScheme.background

    var secondColor by remember { mutableStateOf(themeBackgroundColor) }
    val animateSecondColor by animateColorAsState(secondColor, label = String.EMPTY)
    val brushColorList = remember(animateSecondColor) { mutableStateListOf(themeBackgroundColor, animateSecondColor) }

    val onPaletteResult: (Palette) -> Unit = remember {
        { palette ->
            // difficult to consider all possible pictures and their colors to look universally good for dark and light themes
            (palette.mutedSwatch ?: palette.dominantSwatch)?.let {
                secondColor = Color(it.rgb)
            }
        }
    }

    when {
        configuration.isPortrait() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(brushColorList)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                MainContent(playState, item.image, onToggleAudio, onPaletteResult)
            }
        }

        else -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(brushColorList))
                    .padding(bottom = 46.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MainContent(playState, item.image, onToggleAudio, onPaletteResult)
            }
        }
    }
}

@Composable
private fun MainContent(
    playState: ScreenPlayState,
    imageUrl: String,
    onToggleAudio: (Bitmap?) -> Unit,
    onPaletteResult: (Palette) -> Unit
) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    PlayerArtwork(
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer {
                shadowElevation = 16f
                clip = true
                shape = RoundedCornerShape(16.dp)
            }
            .testTag(PlayerScreenTestTags.ARTWORK),
        imageUrl = imageUrl,
        onImageLoaded = {
            imageBitmap = it
            Palette.from(it).generate { paletteOrNull ->
                paletteOrNull?.let { palette ->
                    onPaletteResult.invoke(palette)
                }
            }
        }
    )

    Box(Modifier.size(60.dp)) {
        if (playState == ScreenPlayState.LOADING) {
            CircularProgressIndicator(
                modifier = Modifier.testTag(PlayerScreenTestTags.PLAY_BUTTON_LOADER),
                strokeCap = StrokeCap.Round
            )
        } else {
            PlayButton(
                isPlaying = playState == ScreenPlayState.PLAYING,
                iconColor = MaterialTheme.colorScheme.primary,
                radius = 40.dp,
                onTogglePlay = { onToggleAudio.invoke(imageBitmap) }
            )
        }
    }
}