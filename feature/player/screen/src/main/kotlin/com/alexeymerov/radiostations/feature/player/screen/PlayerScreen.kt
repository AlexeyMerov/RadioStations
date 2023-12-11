package com.alexeymerov.radiostations.feature.player.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
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
    stationName: String,
    locationName: String,
    stationImgUrl: String,
    rawUrl: String,
    id: String,
    isFav: Boolean
) {
    ComposedTimberD("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
    if (isVisibleToUser) {
        TopBarSetup(
            topBarBlock = topBarBlock,
            stationName = stationName,
            locationName = locationName,
            id = id,
            isFav = isFav,
            onAction = { viewModel.setAction(it) }
        )
    }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val bottomPlayerMedia by viewModel.currentAudioItem.collectAsStateWithLifecycle()

    PlayerScreen(
        viewState = viewState,
        stationImgUrl = stationImgUrl,
        onToggleAudio = { currentItem ->
            var action: ViewAction = ViewAction.ToggleAudio
            if (bottomPlayerMedia?.directUrl != currentItem.directUrl) {
                action = ViewAction.ChangeAudio(currentItem)
            }

            viewModel.setAction(action)
        }
    )

    LaunchedEffect(Unit) { viewModel.setAction(ViewAction.LoadAudio(rawUrl)) }
}

@Composable
private fun TopBarSetup(
    topBarBlock: (TopBarState) -> Unit,
    stationName: String,
    locationName: String,
    id: String,
    isFav: Boolean,
    onAction: (ViewAction) -> Unit
) {
    var isFavorite by rememberSaveable { mutableStateOf(isFav) } // not the best way, consider VM param
    LaunchedEffect(Unit, isFavorite) {
        topBarBlock.invoke(
            TopBarState(
                title = stationName,
                subTitle = locationName,
                displayBackButton = true,
                rightIcon = RightIconItem(if (isFavorite) TopBarIcon.STAR else TopBarIcon.STAR_OUTLINE).apply {
                    action = {
                        isFavorite = !isFavorite
                        onAction.invoke(ViewAction.ToggleFavorite(id))
                    }
                }
            )
        )
    }
}

@Composable
private fun PlayerScreen(
    viewState: ViewState,
    stationImgUrl: String,
    onToggleAudio: (AudioItemDto) -> Unit
) {
    when (viewState) {
        is ViewState.Loading -> LoaderView()
        is ViewState.Error -> ErrorView()
        is ViewState.ReadyToPlay -> {
            MainContentWithOrientation(
                isPlaying = viewState.isPlaying,
                isLoading = viewState.isLoading,
                imageUrl = stationImgUrl,
                onToggleAudio = { onToggleAudio.invoke(viewState.item) }
            )
        }
    }
}

@Composable
private fun MainContentWithOrientation(isPlaying: Boolean, isLoading: Boolean, imageUrl: String, onToggleAudio: () -> Unit) {
    val configuration = LocalConfiguration.current

    when {
        configuration.isPortrait() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                MainContent(isPlaying, isLoading, imageUrl, onToggleAudio)
            }
        }

        else -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MainContent(isPlaying, isLoading, imageUrl, onToggleAudio)
            }
        }
    }
}

@Composable
private fun MainContent(isPlaying: Boolean, isLoading: Boolean, imageUrl: String, onToggleAudio: () -> Unit) {
    PlayerArtwork(
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer {
                shadowElevation = 16f
                clip = true
                shape = RoundedCornerShape(16.dp)
            },
        imageUrl = imageUrl
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
fun PlayerArtwork(modifier: Modifier, imageUrl: String) {
    var isLoaded by rememberSaveable { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val colorFilter: ColorFilter? by remember(isLoaded) {
        derivedStateOf {
            if (isLoaded) null else ColorFilter.tint(colorScheme.primary)
        }
    }

    AsyncImage(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(500)
            .build(),
        contentDescription = null,
        error = painterResource(id = R.drawable.icon_radio),
        colorFilter = colorFilter,
        onSuccess = { isLoaded = true },
        onError = { isLoaded = false }
    )
}

@Composable
fun PlayerControlButton(isPlaying: Boolean, onToggleAudio: () -> Unit) {
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

    LottieAnimation(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
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