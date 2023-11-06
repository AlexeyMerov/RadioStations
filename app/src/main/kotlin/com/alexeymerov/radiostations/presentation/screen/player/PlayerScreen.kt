package com.alexeymerov.radiostations.presentation.screen.player

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
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
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.CallOnDispose
import com.alexeymerov.radiostations.common.CallOnLaunch
import com.alexeymerov.radiostations.presentation.common.ErrorView
import com.alexeymerov.radiostations.presentation.common.LoaderView
import com.alexeymerov.radiostations.presentation.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.screen.player.PlayerViewModel.PlayerState
import com.alexeymerov.radiostations.presentation.screen.player.PlayerViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.player.PlayerViewModel.ViewState
import timber.log.Timber

@Composable
fun BasePlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    isVisibleToUser: Boolean,
    topBarBlock: (TopBarState) -> Unit,
    stationName: String,
    locationName: String,
    stationImgUrl: String,
    rawUrl: String,
    id: String,
    isFav: Boolean
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")
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
    val playState by viewModel.playerState.collectAsStateWithLifecycle()
    PlayerScreen(
        viewState = viewState,
        stationImgUrl = stationImgUrl,
        rawUrl = rawUrl,
        playState = playState,
        onAction = { viewModel.setAction(it) }
    )
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
                rightIcon = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                rightIconAction = {
                    isFavorite = !isFavorite
                    onAction.invoke(ViewAction.ToggleFavorite(id))
                }
            )
        )
    }
}

@Composable
private fun PlayerScreen(
    viewState: ViewState,
    stationImgUrl: String,
    rawUrl: String,
    playState: PlayerState,
    onAction: (ViewAction) -> Unit
) {
    when (viewState) {
        is ViewState.Loading -> LoaderView()
        is ViewState.Error -> ErrorView()
        is ViewState.ReadyToPlay -> {
            MainContent(
                playState = playState,
                imageUrl = stationImgUrl,
                stationUrl = viewState.url,
                onToggleAudio = { onAction.invoke(ViewAction.ToggleAudio) }
            )
        }
    }

    CallOnLaunch { onAction.invoke(ViewAction.LoadAudio(rawUrl)) }
}

@Composable
private fun MainContent(playState: PlayerState, imageUrl: String, stationUrl: String, onToggleAudio: () -> Unit) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                StationImage(imageUrl)
                ControlButton(playState, onToggleAudio)
            }
        }

        else -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StationImage(imageUrl)
                ControlButton(playState, onToggleAudio)
            }
        }
    }
    ProcessPlayerState(playState, stationUrl)
}

@Composable
private fun ProcessPlayerState(playState: PlayerState, stationUrl: String) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] audioLink $stationUrl")

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(stationUrl))
            prepare()
            playWhenReady = true
        }
    }
    CallOnDispose {
        exoPlayer.stop()
        exoPlayer.release()
    }

    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] playState ${playState.javaClass.simpleName}")
    if (playState == PlayerState.Playing) {
        exoPlayer.play()
    } else {
        exoPlayer.pause()
    }
}

@Composable
private fun StationImage(imageUrl: String) {
    Card(
        modifier = Modifier
            .size(250.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(500)
                .build(),
            contentDescription = null,
            error = painterResource(id = R.drawable.full_image)
        )
    }
}

@Composable
private fun ControlButton(playState: PlayerState, onToggleAudio: () -> Unit) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playstop))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        speed = playState.lottieSpeed,
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
            .size(60.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onBackground,
                    bounded = false,
                    radius = 40.dp
                ),
                onClick = onToggleAudio::invoke
            ),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = { animationStateProgress }
    )
}