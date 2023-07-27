package com.alexeymerov.radiostations.presentation.screen.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
//@Preview
fun PlayerScreen(
    stationImgUrl: String,
    rawUrl: String,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ")

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    when (val state = viewState) {
        ViewState.Loading -> LoaderView()
        ViewState.Error -> ErrorView()
        is ViewState.ReadyToPlay -> MainContent(viewModel, stationImgUrl, state.url)
    }

    CallOnLaunch { viewModel.setAction(PlayerViewModel.ViewAction.LoadAudio(rawUrl)) }
}

@Composable
private fun MainContent(viewModel: PlayerViewModel, imageUrl: String, stationUrl: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StationImage(imageUrl)
        ControlButton(viewModel)
        ProcessPlayerState(viewModel, stationUrl)
    }
}

@Composable
private fun ProcessPlayerState(viewModel: PlayerViewModel, stationUrl: String) {
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

    val playState by viewModel.playerState.collectAsStateWithLifecycle()
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
            .fillMaxWidth()
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
private fun ControlButton(viewModel: PlayerViewModel) {
    val playState by viewModel.playerState.collectAsStateWithLifecycle()

    val resId: Int
    val contentDescriptionString: String
    if (playState == PlayerViewModel.PlayerState.Play) {
        resId = R.drawable.stop_square
        contentDescriptionString = stringResource(R.string.stop)
    } else {
        resId = R.drawable.play_arrow
        contentDescriptionString = stringResource(R.string.play)
    }

    val vectorPainter = rememberVectorPainter(ImageVector.vectorResource(id = resId))
    val description by rememberSaveable(playState) { mutableStateOf(contentDescriptionString) }

    Image(
        vectorPainter,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
        contentDescription = description,
        modifier = Modifier
            .size(60.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onBackground,
                    bounded = false,
                    radius = 80.dp
                ),
            ) {
                viewModel.setAction(PlayerViewModel.ViewAction.ToggleAudio)
            }
    )
}