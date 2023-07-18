package com.alexeymerov.radiostations.presentation.fragment.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.presentation.fragment.item.AudioViewModel.ViewState
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//@Preview
fun AudioScreen(
    navController: NavHostController,
    stationName: String,
    stationImgUrl: String,
    rawUrl: String,
    viewModel: AudioViewModel = hiltViewModel()
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

    Scaffold(
        containerColor = colorResource(R.color.background),
        topBar = {
            TopAppBar(
                title = { Text(stationName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.main_200),
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        content = { padding ->
            if (isLoading) LoaderView() else MainContent(viewModel, stationImgUrl, padding)

            when (val state = viewState) {
                ViewState.Error -> Error()
                is ViewState.ReadyToPlay -> ProcessReadyToPlay(viewModel, exoPlayer, state)
                else -> {}
            }
        }
    )

    viewModel.setAction(AudioViewModel.ViewAction.LoadAudio(rawUrl))

}

@Composable
private fun ProcessReadyToPlay(viewModel: AudioViewModel, exoPlayer: ExoPlayer, state: ViewState.ReadyToPlay) {
    Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] audioLink ${state.url}")
    val mediaItem = MediaItem.fromUri(state.url)
    exoPlayer.setMediaItem(mediaItem)

    val playState by viewModel.playerState.collectAsStateWithLifecycle()

    when (playState) {
        AudioViewModel.PlayerState.Play -> {
            exoPlayer.prepare()
            exoPlayer.play()
        }

        AudioViewModel.PlayerState.Stop -> exoPlayer.stop()
    }
}

@Composable
private fun MainContent(viewModel: AudioViewModel, imageUrl: String, padding: PaddingValues) {
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
private fun ControlButton(viewModel: AudioViewModel) {
    val playState by viewModel.playerState.collectAsStateWithLifecycle()
    val resId = when (playState) {
        AudioViewModel.PlayerState.Play -> R.drawable.stop_square
        AudioViewModel.PlayerState.Stop -> R.drawable.play_arrow
    }
    val vectorPlay = ImageVector.vectorResource(id = resId)
    val vectorPlayPainter = rememberVectorPainter(vectorPlay)
    Image(
        vectorPlayPainter,
        contentDescription = null,
        modifier = Modifier
            .size(64.dp)
            .clickable {
                viewModel.setAction(AudioViewModel.ViewAction.ToggleAudio)
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