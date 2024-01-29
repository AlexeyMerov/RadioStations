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
import com.alexeymerov.radiostations.core.common.EMPTY
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
fun PreloadedPlayerScreen(
    viewModel: PlayerViewModel,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    stationName: String,
    subTitle: String,
    stationImgUrl: String,
    rawUrl: String,
    id: String
) {
    ComposedTimberD("[ PreloadedPlayerScreen ] ")

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val bottomPlayerMedia by viewModel.currentAudioItem.collectAsStateWithLifecycle()

    BasePlayerScreen(
        viewState = viewState,
        playerMediaDirectUrl = bottomPlayerMedia?.directUrl,
        isVisibleToUser = isVisibleToUser,
        topBarBlock = topBarBlock,
        initStationName = stationName,
        initSubTitle = subTitle,
        initId = id,
        isFavorite = viewModel.isFavorite,
        initStationImgUrl = stationImgUrl,
        initRawUrl = rawUrl,
        onAction = { viewModel.setAction(it) }
    )
}

@Composable
fun LoadPlayerScreen(
    viewModel: PlayerViewModel,
    isVisibleToUser: Boolean,
    parentUrl: String,
    topBarBlock: (TopBarState) -> Unit
) {
    ComposedTimberD("[ LoadPlayerScreen ] ")

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val bottomPlayerMedia by viewModel.currentAudioItem.collectAsStateWithLifecycle()

    BasePlayerScreen(
        viewState = viewState,
        isVisibleToUser = isVisibleToUser,
        topBarBlock = topBarBlock,
        playerMediaDirectUrl = bottomPlayerMedia?.directUrl,
        isFavorite = viewModel.isFavorite,
        onAction = { viewModel.setAction(it) }
    )

    LaunchedEffect(Unit) { viewModel.setAction(ViewAction.LoadStationInfo(parentUrl)) }
}

@Composable
private fun BasePlayerScreen(
    viewState: ViewState,
    playerMediaDirectUrl: String?,
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    initStationName: String = String.EMPTY,
    initSubTitle: String = String.EMPTY,
    initId: String = String.EMPTY,
    isFavorite: Boolean,
    initStationImgUrl: String = String.EMPTY,
    initRawUrl: String = String.EMPTY,
    onAction: (ViewAction) -> Unit
) {
    ComposedTimberD("[ BasePlayerScreen ] ")

    var stationName by rememberSaveable { mutableStateOf(initStationName) }
    var subTitle by rememberSaveable { mutableStateOf(initSubTitle) }
    var id by rememberSaveable { mutableStateOf(initId) }
    var stationImgUrl by rememberSaveable { mutableStateOf(initStationImgUrl) }
    var rawUrl by rememberSaveable { mutableStateOf(initRawUrl) }

    if (isVisibleToUser) {
        TopBarSetup(
            topBarBlock = topBarBlock,
            stationName = stationName,
            subTitle = subTitle,
            id = id,
            isFavorite = isFavorite,
            onAction = { onAction.invoke(it) }
        )
    }

    when (viewState) {
        is ViewState.Loading -> LoaderView()
        is ViewState.Error -> ErrorView()
        is ViewState.Loaded -> {
            LoaderView()

            stationName = viewState.item.text
            subTitle = viewState.item.subTitle.orEmpty()
            id = viewState.item.id
            stationImgUrl = viewState.item.image.orEmpty()
            rawUrl = viewState.item.url
        }

        is ViewState.ReadyToPlay -> {
            MainContentWithOrientation(
                isPlaying = viewState.isPlaying,
                isLoading = viewState.isLoading,
                imageUrl = stationImgUrl,
                onToggleAudio = {
                    var action: ViewAction = ViewAction.ToggleAudio
                    if (playerMediaDirectUrl != viewState.item.directUrl) {
                        action = ViewAction.ChangeAudio(viewState.item)
                    }

                    onAction.invoke(action)
                }
            )
        }
    }
    if (rawUrl.isNotEmpty()) {
        LaunchedEffect(Unit) { onAction.invoke(ViewAction.LoadAudio(rawUrl)) }
    }
}

@Composable
private fun TopBarSetup(
    topBarBlock: (TopBarState) -> Unit,
    stationName: String,
    subTitle: String,
    id: String,
    isFavorite: Boolean,
    onAction: (ViewAction) -> Unit
) {
    LaunchedEffect(Unit, stationName, isFavorite) {
        topBarBlock.invoke(
            TopBarState(
                title = stationName,
                subTitle = subTitle,
                displayBackButton = true,
                rightIcon = RightIconItem(if (isFavorite) TopBarIcon.STAR else TopBarIcon.STAR_OUTLINE).apply {
                    action = { onAction.invoke(ViewAction.ToggleFavorite(id)) }
                }
            )
        )
    }
}

@Composable
private fun MainContentWithOrientation(isPlaying: Boolean, isLoading: Boolean, imageUrl: String, onToggleAudio: () -> Unit) {
    val configuration = LocalConfiguration.current
    val themeBackgroundColor = MaterialTheme.colorScheme.background
    var backgroundColors by remember {
        mutableStateOf(listOf(themeBackgroundColor, themeBackgroundColor)) // required 2 by Brush
    }
    val onPaletteResult: (Palette) -> Unit = { palette ->
        // difficult to consider all possible pictures and their colors to look universally good for dark and light themes
        (palette.mutedSwatch ?: palette.dominantSwatch)?.let {
            backgroundColors = listOf(
                themeBackgroundColor,
                Color(it.rgb)
            )
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
                MainContent(isPlaying, isLoading, imageUrl, onToggleAudio, onPaletteResult)
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
                MainContent(isPlaying, isLoading, imageUrl, onToggleAudio, onPaletteResult)
            }
        }
    }
}

@Composable
private fun MainContent(
    isPlaying: Boolean,
    isLoading: Boolean,
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