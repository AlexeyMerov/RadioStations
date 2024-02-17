package com.alexeymerov.radiostations.core.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.extensions.shimmerEffect

@Composable
fun LoaderView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(CommonViewTestTags.LOADER),
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
fun ShimmerLoading(modifier: Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        repeat(7) {
            Box(
                modifier = modifier.shimmerEffect(shape = CardDefaults.shape),
                content = {}
            )
        }
    }
}