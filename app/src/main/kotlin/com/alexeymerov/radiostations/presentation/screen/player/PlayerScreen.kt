package com.alexeymerov.radiostations.presentation.screen.player

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.presentation.MainViewModel
import com.alexeymerov.radiostations.presentation.common.CallOnLaunch
import com.alexeymerov.radiostations.presentation.common.view.ErrorView
import com.alexeymerov.radiostations.presentation.common.view.LoaderView
import com.alexeymerov.radiostations.presentation.navigation.RightIconItem
import com.alexeymerov.radiostations.presentation.navigation.TopBarIcon
import com.alexeymerov.radiostations.presentation.navigation.TopBarState
import com.alexeymerov.radiostations.presentation.screen.player.PlayerViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.player.PlayerViewModel.ViewState
import timber.log.Timber

@Composable
fun BasePlayerScreen(
    viewModel: PlayerViewModel,
    mainViewModel: MainViewModel,
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
    val bottomPlayerMedia by mainViewModel.currentAudioItem.collectAsStateWithLifecycle()

    PlayerScreen(
        viewState = viewState,
        stationImgUrl = stationImgUrl,
        onToggleAudio = { currentItem ->
            var action: MainViewModel.ViewAction = MainViewModel.ViewAction.ToggleAudio
            if (bottomPlayerMedia?.directUrl != currentItem.directUrl) {
                action = MainViewModel.ViewAction.ChangeAudio(currentItem)
            }

            mainViewModel.setAction(action)
        }
    )

    CallOnLaunch { viewModel.setAction(ViewAction.LoadAudio(rawUrl)) }
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
            MainContent(
                isPlaying = viewState.isPlaying,
                imageUrl = stationImgUrl,
                onToggleAudio = { onToggleAudio.invoke(viewState.item) }
            )
        }
    }
}

@Composable
private fun MainContent(isPlaying: Boolean, imageUrl: String, onToggleAudio: () -> Unit) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                StationImage(imageUrl)
                ControlButton(isPlaying, onToggleAudio)
            }
        }

        else -> {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StationImage(imageUrl)
                ControlButton(isPlaying, onToggleAudio)
            }
        }
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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
private fun ControlButton(isPlaying: Boolean, onToggleAudio: () -> Unit) {
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