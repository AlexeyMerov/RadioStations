package com.alexeymerov.radiostations.feature.player.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.isPortrait
import com.alexeymerov.radiostations.core.ui.navigation.RightIconItem
import com.alexeymerov.radiostations.core.ui.navigation.TopBarIcon
import com.alexeymerov.radiostations.core.ui.navigation.TopBarState
import com.alexeymerov.radiostations.core.ui.view.ComposedTimberD
import com.alexeymerov.radiostations.core.ui.view.ErrorView
import com.alexeymerov.radiostations.core.ui.view.LoaderView
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ViewAction
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ViewState

@Composable
fun BasePlayerScreen(
    viewModel: PlayerViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    stationName: String
) {
    ComposedTimberD("BasePlayerScreen")

    if (isVisibleToUser) {
        TopBarSetup(
            topBarBlock = topBarBlock,
            stationName = stationName,
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
private fun PlayerScreen(
    viewState: ViewState,
    onAction: (ViewAction) -> Unit
) {
    when (viewState) {
        is ViewState.Loading -> LoaderView()
        is ViewState.Error -> ErrorView()
        is ViewState.ReadyToPlay -> {
            MainContentWithOrientation(
                item = viewState.item,
                isSameItem = viewState.isSameItem,
                playState = viewState.playState,
                onToggleAudio = { onAction.invoke(ViewAction.ChangeOrToggleAudio(viewState.item)) }
            )
        }
    }
}

@Composable
private fun TopBarSetup(
    topBarBlock: (TopBarState) -> Unit,
    stationName: String,
    subTitle: String?,
    isFavorite: Boolean?,
    onAction: (ViewAction) -> Unit
) {
    LaunchedEffect(Unit, subTitle, isFavorite) {
        topBarBlock.invoke(
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
    isSameItem: Boolean,
    playState: AudioUseCase.PlayerState,
    onToggleAudio: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val themeBackgroundColor = MaterialTheme.colorScheme.background
    var backgroundColors by remember {
        mutableStateOf(listOf(themeBackgroundColor, themeBackgroundColor)) // required 2 by Brush
    }
    val onPaletteResult: (Palette) -> Unit = remember {
        { palette ->
            // difficult to consider all possible pictures and their colors to look universally good for dark and light themes
            (palette.mutedSwatch ?: palette.dominantSwatch)?.let {
                backgroundColors = listOf(
                    themeBackgroundColor,
                    Color(it.rgb)
                )
            }
        }
    }

    when {
        configuration.isPortrait() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(backgroundColors)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                MainContent(isSameItem, playState, item.image, onToggleAudio, onPaletteResult)
            }
        }

        else -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(backgroundColors))
                    .padding(bottom = 46.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MainContent(isSameItem, playState, item.image, onToggleAudio, onPaletteResult)
            }
        }
    }
}

@Composable
private fun MainContent(
    isSameItem: Boolean,
    playState: AudioUseCase.PlayerState,
    imageUrl: String,
    onToggleAudio: () -> Unit,
    onPaletteResult: (Palette) -> Unit
) {
    PlayerArtwork(
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer {
                shadowElevation = 16f
                clip = true
                shape = RoundedCornerShape(16.dp)
            },
        imageUrl = imageUrl,
        onPaletteResult = onPaletteResult
    )

    val isPlaying by remember(isSameItem, playState) {
        derivedStateOf {
            isSameItem && playState == AudioUseCase.PlayerState.PLAYING
        }
    }

    val isLoading by remember(isSameItem, playState) {
        derivedStateOf {
            isSameItem && playState == AudioUseCase.PlayerState.LOADING
        }
    }

    Box(Modifier.size(60.dp)) {
        if (isLoading) {
            CircularProgressIndicator(strokeCap = StrokeCap.Round)
        } else {
            PlayerControlButton(isPlaying, onToggleAudio)
        }
    }
}

@Composable
internal fun PlayerArtwork(
    modifier: Modifier,
    imageUrl: String,
    onPaletteResult: ((Palette) -> Unit)? = null
) {
    var isLoaded by rememberSaveable { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val colorFilter: ColorFilter? by remember(isLoaded) {
        derivedStateOf {
            if (isLoaded) null else ColorFilter.tint(colorScheme.primary)
        }
    }

    AsyncImage(
        modifier = modifier.background(Color.White),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .allowHardware(false)
            .crossfade(500)
            .build(),
        contentDescription = null,
        error = rememberAsyncImagePainter(R.drawable.icon_radio),
        colorFilter = colorFilter,
        onSuccess = {
            val bitmap = it.result.drawable.toBitmap()
            Palette.from(bitmap).generate { paletteOrNull ->
                paletteOrNull?.let { palette ->
                    onPaletteResult?.invoke(palette)
                }
            }
            isLoaded = true
        },
        onError = { isLoaded = false }
    )
}

@Composable
internal fun PlayerControlButton(isPlaying: Boolean, onToggleAudio: () -> Unit) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playstop))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        speed = if (isPlaying) -2f else 2f,
        clipSpec = LottieClipSpec.Progress(max = 0.5f)
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.primary.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    val interactionSource = remember { MutableInteractionSource() }
    LottieAnimation(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onBackground,
                    bounded = false,
                    radius = 40.dp
                ),
                onClick = { onToggleAudio.invoke() }
            ),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = { animationStateProgress }
    )
}