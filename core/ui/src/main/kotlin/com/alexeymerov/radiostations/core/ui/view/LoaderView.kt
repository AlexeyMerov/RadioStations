package com.alexeymerov.radiostations.core.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun ShimmerLoadingSimple(modifier: Modifier) {
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

@Composable
fun ShimmerLoading(modifier: Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .testTag(CommonViewTestTags.SHIMMER),
    ) {

        val cardShape = CardDefaults.shape
        val smallShape = MaterialTheme.shapes.small
        val mediumShape = MaterialTheme.shapes.medium

        val shimmerEffect = remember { Modifier.shimmerEffect(shape = cardShape) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val headerModifier = remember {
                Modifier
                    .shimmerEffect(shape = smallShape)
                    .height(28.dp)
            }

            Box(headerModifier.width(80.dp))

            Spacer(Modifier.width(16.dp))

            Box(headerModifier.width(60.dp))

            Spacer(Modifier.width(16.dp))

            Box(headerModifier.width(90.dp))
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = shimmerEffect
                .height(18.dp)
                .width(70.dp)
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = CardDefaults.shape
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            Box(
                shimmerEffect
                    .height(22.dp)
                    .width(250.dp)
            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = CardDefaults.shape
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))

            Box(
                modifier = shimmerEffect
                    .height(22.dp)
                    .width(175.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = shimmerEffect
                .height(18.dp)
                .width(85.dp)
        )

        Spacer(Modifier.height(8.dp))

        val imageModifier = remember {
            Modifier
                .shimmerEffect(
                    shape = mediumShape.copy(
                        topEnd = CornerSize(0),
                        bottomEnd = CornerSize(0)
                    )
                )
                .fillMaxHeight()
                .aspectRatio(1f)
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = CardDefaults.shape
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = imageModifier)

            Spacer(Modifier.width(16.dp))

            Box(
                modifier = shimmerEffect
                    .height(22.dp)
                    .width(175.dp)
            )

            Spacer(Modifier.weight(1f))

            Box(shimmerEffect.size(24.dp))

            Spacer(Modifier.width(16.dp))
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = CardDefaults.shape
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = imageModifier)

            Column(Modifier.padding(start = 16.dp)) {
                Box(
                    modifier = shimmerEffect
                        .height(20.dp)
                        .width(125.dp)
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = shimmerEffect
                        .height(14.dp)
                        .width(85.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Box(shimmerEffect.size(24.dp))

            Spacer(Modifier.width(16.dp))
        }

    }
}