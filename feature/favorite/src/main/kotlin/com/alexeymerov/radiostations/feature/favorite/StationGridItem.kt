package com.alexeymerov.radiostations.feature.favorite

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags.STAR_ICON
import com.alexeymerov.radiostations.core.ui.view.FlipBox
import com.alexeymerov.radiostations.core.ui.view.SelectedIcon
import com.alexeymerov.radiostations.core.ui.view.StationImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StationGridItem(
    modifier: Modifier,
    itemDto: CategoryItemDto,
    inSelection: Boolean,
    isSelected: Boolean,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit,
    onLongClick: (CategoryItemDto) -> Unit = {}
) {
    Card(
        modifier = modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                    onClick = { onAudioClick.invoke(itemDto) },
                    onLongClick = { onLongClick.invoke(itemDto) }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlipBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                isFlipped = isSelected,
                frontSide = { ImageContent(itemDto, inSelection, onFavClick) },
                backSide = { SelectedIcon() }
            )

            BasicText(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .basicMarquee(
                        iterations = 10,
                        velocity = 20.dp
                    ),
                text = itemDto.text
            )

            itemDto.subTitle?.let { subtext ->
                Row(
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (itemDto.hasLocation()) {
                        Icon(
                            modifier = Modifier
                                .alpha(0.7f)
                                .size(16.dp)
                                .padding(end = 4.dp, bottom = 1.dp),
                            imageVector = Icons.Outlined.LocationCity,
                            contentDescription = null
                        )
                    }

                    BasicText(
                        modifier = Modifier.alpha(0.7f),
                        text = subtext,
                        textStyle = MaterialTheme.typography.labelMedium
                    )
                }

            }
        }
    }
}

@Composable
private fun ImageContent(
    itemDto: CategoryItemDto,
    inSelection: Boolean,
    onFavClick: (CategoryItemDto) -> Unit
) {
    Box {
        StationImage(
            modifier = Modifier.fillMaxSize(),
            itemDto = itemDto
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = !inSelection
        ) {
            AnimatedContent(
                targetState = itemDto.isFavorite,
                label = "Star FavIcon",
                transitionSpec = { scaleIn().togetherWith(scaleOut()) }
            ) {
                IconButton(
                    modifier = Modifier.testTag(STAR_ICON),
                    onClick = { onFavClick.invoke(itemDto) }) {
                    Icon(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        ),
                        imageVector = if (it) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

    }
}