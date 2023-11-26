package com.alexeymerov.radiostations.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.domain.usecase.audio.AudioUseCase
import com.alexeymerov.radiostations.presentation.MainViewModel
import com.alexeymerov.radiostations.presentation.common.view.BasicText

@Composable
fun BottomPlayer(
    playerState: AudioUseCase.PlayerState,
    playerTitle: String,
    onPlayerAction: (MainViewModel.ViewAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.secondary),

        ) {
        val isVisible = playerState != AudioUseCase.PlayerState.EMPTY
        AnimatedVisibility(visible = isVisible) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isPlaying = playerState == AudioUseCase.PlayerState.PLAYING

                PlayingWaves(isPlaying)

                BasicText(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f),
                    text = playerTitle,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                PlayButton(
                    isPlaying = isPlaying,
                    onTogglePlay = { onPlayerAction.invoke(MainViewModel.ViewAction.ToggleAudio) }
                )

                IconButton(
                    modifier = Modifier.padding(start = 4.dp),
                    onClick = { onPlayerAction.invoke(MainViewModel.ViewAction.NukePlayer) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        contentDescription = String.EMPTY
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayingWaves(isPlaying: Boolean) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playing))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE,
        isPlaying = isPlaying,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSecondary.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        modifier = Modifier.size(25.dp),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = {
            if (isPlaying) animationStateProgress else 0f
        },
    )
}

@Composable
private fun PlayButton(isPlaying: Boolean, onTogglePlay: () -> Unit) {
    val lotSpeed = if (isPlaying) -2f else 2f
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.playstop))
    val animationStateProgress by animateLottieCompositionAsState(
        composition = composition,
        speed = lotSpeed,
        clipSpec = LottieClipSpec.Progress(max = 0.5f)
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onSecondary.toArgb(),
            keyPath = arrayOf("**")
        )
    )

    LottieAnimation(
        modifier = Modifier
            .size(35.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onSecondary,
                    bounded = false,
                    radius = 20.dp
                ),
                onClick = { onTogglePlay.invoke() }
            ),
        composition = composition,
        dynamicProperties = dynamicProperties,
        progress = { animationStateProgress }
    )
}
