package com.alexeymerov.radiostations.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexeymerov.radiostations.R

@Composable
fun ErrorView(errorText: String = stringResource(R.string.sorry_nothing_found)) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sad))
        val animationState = animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

        LottieAnimation(
            modifier = Modifier.size(150.dp),
            composition = composition,
            progress = { animationState.progress }
        )

        AnimatedVisibility(
            visible = animationState.isPlaying,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            Text(
                text = errorText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
        }
    }
}