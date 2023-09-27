package com.alexeymerov.radiostations.presentation.screen.player

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.common.CallOnDispose
import com.alexeymerov.radiostations.presentation.common.CallOnLaunch
import com.alexeymerov.radiostations.presentation.common.ErrorView
import com.alexeymerov.radiostations.presentation.common.LoaderView
import com.alexeymerov.radiostations.presentation.screen.player.PlayerViewModel.ViewState
import timber.log.Timber

@Composable
fun PlayerScreen(
    stationImgUrl: String,
    rawUrl: String,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    when (val state = viewState) {
        is ViewState.Loading -> LoaderView()
        is ViewState.Error -> ErrorView()
        is ViewState.ReadyToPlay -> {
            val playState by viewModel.playerState.collectAsStateWithLifecycle()
            MainContent(playState, stationImgUrl, state.url) {
                viewModel.setAction(PlayerViewModel.ViewAction.ToggleAudio)
            }
        }
    }

    CallOnLaunch { viewModel.setAction(PlayerViewModel.ViewAction.LoadAudio(rawUrl)) }
}

@Composable
private fun MainContent(playState: PlayerViewModel.PlayerState, imageUrl: String, stationUrl: String, onToggleAudio: () -> Unit) {
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
private fun ProcessPlayerState(playState: PlayerViewModel.PlayerState, stationUrl: String) {
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
    if (playState == PlayerViewModel.PlayerState.Play) {
        exoPlayer.play()
    } else {
        exoPlayer.pause()
    }
}

@Composable
private fun StationImage(imageUrl: String) {
    AsyncImage(
        modifier = Modifier
            .height(300.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(500)
            .build(),
        contentDescription = null,
        error = painterResource(id = R.drawable.full_image)
    )
}

@Composable
private fun ControlButton(playState: PlayerViewModel.PlayerState, onToggleAudio: () -> Unit) {
    val descriptionString = stringResource(playState.contentDescription)
    val description by rememberSaveable(playState) { mutableStateOf(descriptionString) }
    val vectorPainter = rememberVectorPainter(ImageVector.vectorResource(id = playState.iconResId))

    Image(
        vectorPainter,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
        contentDescription = description,
        modifier = Modifier
            .size(60.dp)
            .clickable(
                interactionSource = MutableInteractionSource(), indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onBackground,
                    bounded = false,
                    radius = 80.dp
                ),
                onClick = onToggleAudio::invoke
            )
    )
}