package com.alexeymerov.radiostations.presentation.screen.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType

@Composable
fun StationListItem(modifier: Modifier, itemDto: CategoryItemDto, onAudioClick: (CategoryItemDto) -> Unit, onFavClick: (CategoryItemDto) -> Unit) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { onAudioClick.invoke(itemDto) }
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val placeholderPainter = painterResource(id = R.drawable.full_image)
            AsyncImage(
                modifier = Modifier
                    .size(65.dp)
                    .background(MaterialTheme.colorScheme.background),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(itemDto.image)
                    .crossfade(500)
                    .build(),
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
                error = placeholderPainter,
                placeholder = placeholderPainter
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                BasicText(text = itemDto.text)

                itemDto.subText?.let { subtext ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .alpha(0.7f)
                                .size(12.dp),
                            imageVector = Icons.Outlined.LocationCity,
                            contentDescription = String.EMPTY
                        )

                        BasicText(
                            modifier = Modifier
                                .alpha(0.7f)
                                .padding(start = 4.dp),
                            text = subtext,
                            textStyle = MaterialTheme.typography.labelMedium
                        )
                    }

                }
            }


            AnimatedContent(
                targetState = itemDto.isFavorite,
                label = "Star",
                transitionSpec = { scaleIn().togetherWith(scaleOut()) }
            ) {
                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { onFavClick.invoke(itemDto) }
                ) {
                    Icon(
                        imageVector = if (it) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = String.EMPTY,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StationListItemPreview() {
    val item = CategoryItemDto(
        url = "",
        originalText = "",
        subText = "Hello",
        text = "Station NameStation NameStation NameStation Name",
        type = DtoItemType.AUDIO,
        isFavorite = true
    )
    StationListItem(Modifier.fillMaxWidth(), item, {}, {})
}