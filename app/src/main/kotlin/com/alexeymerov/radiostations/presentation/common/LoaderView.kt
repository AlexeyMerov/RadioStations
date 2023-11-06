package com.alexeymerov.radiostations.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexeymerov.radiostations.R

@Composable
fun LoaderView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
        LottieAnimation(
            modifier = Modifier.size(150.dp),
            composition = composition,
            speed = 1.5f,
            iterations = LottieConstants.IterateForever
        )
    }
}

@Composable
fun ShimmerLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shimmerEffect(shape = CardDefaults.shape),
                content = {}
            )
        }
    }
}