package com.alexeymerov.radiostations.presentation.screen.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.alexeymerov.radiostations.R
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

    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val isLoading = viewState == ViewState.Loading

    CreateMainContent(isLoading, viewModel, stationImgUrl, viewState, exoPlayer)

    viewModel.setAction(PlayerViewModel.ViewAction.LoadAudio(rawUrl))
}

@Composable
private fun CreateMainContent(
    isLoading: Boolean,
    viewModel: PlayerViewModel,
    stationImgUrl: String,
    viewState: ViewState,
    exoPlayer: ExoPlayer
) {
    if (isLoading) LoaderView() else MainContent(viewModel, stationImgUrl)

    when (viewState) {
        ViewState.Error -> Error()
        is ViewState.ReadyToPlay -> ProcessReadyToPlay(viewModel, exoPlayer, viewState)
        else -> {}
    }
}

@Composable
private fun ProcessReadyToPlay(viewModel: PlayerViewModel, exoPlayer: ExoPlayer, state: ViewState.ReadyToPlay) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] audioLink ${state.url}")
    val mediaItem = MediaItem.fromUri(state.url)
    exoPlayer.setMediaItem(mediaItem)

    val playState by viewModel.playerState.collectAsStateWithLifecycle()
    if (playState == PlayerViewModel.PlayerState.Play) {
        exoPlayer.prepare()
        exoPlayer.play()
    } else {
        exoPlayer.stop()
    }
}

@Composable
private fun MainContent(viewModel: PlayerViewModel, imageUrl: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StationImage(imageUrl)
        ControlButton(viewModel)
    }
}

@Composable
private fun StationImage(imageUrl: String) {
    val vectorPainter = painterResource(id = R.drawable.full_image)
    AsyncImage(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(8.dp)),
        model = imageUrl,
        contentDescription = null,
        placeholder = vectorPainter,
        error = vectorPainter
    )
}

@Composable
private fun ControlButton(viewModel: PlayerViewModel) {
    val playState by viewModel.playerState.collectAsStateWithLifecycle()
    val resId = when (playState) {
        PlayerViewModel.PlayerState.Play -> R.drawable.stop_square
        PlayerViewModel.PlayerState.Stop -> R.drawable.play_arrow
    }
    val vectorPlay = ImageVector.vectorResource(id = resId)
    val vectorPlayPainter = rememberVectorPainter(vectorPlay)
    Image(
        vectorPlayPainter,
        contentDescription = null,
        modifier = Modifier
            .size(64.dp)
            .clickable {
                viewModel.setAction(PlayerViewModel.ViewAction.ToggleAudio)
            }
    )
}

@Composable
private fun LoaderView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(progress = 50f)
    }
}

@Composable
private fun Error() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.nothing_found))
    }
}