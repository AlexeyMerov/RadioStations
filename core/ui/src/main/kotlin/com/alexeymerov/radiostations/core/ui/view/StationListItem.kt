package com.alexeymerov.radiostations.core.ui.view

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.remembers.rememberTextPainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StationListItem(
    modifier: Modifier,
    itemDto: CategoryItemDto,
    inSelection: Boolean,
    isSelected: Boolean,
    onAudioClick: (CategoryItemDto) -> Unit,
    onFavClick: (CategoryItemDto) -> Unit,
    onLongClick: (CategoryItemDto) -> Unit = {}
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                    onClick = { onAudioClick.invoke(itemDto) },
                    onLongClick = { onLongClick.invoke(itemDto) }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlipBox(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                isFlipped = isSelected,
                frontSide = { StationImage(itemDto) },
                backSide = { SelectedIcon() }
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                TextBlock(itemDto)
            }

            AnimatedVisibility(
                visible = !inSelection,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FavIcon(itemDto, onFavClick)
            }
        }
    }
}

@Composable
private fun StationImage(itemDto: CategoryItemDto) {
    val placeholder = rememberTextPainter(
        containerColor = MaterialTheme.colorScheme.background,
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSecondary
        ),
        text = itemDto.initials
    )
    AsyncImage(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        model = ImageRequest.Builder(LocalContext.current)
            .data(itemDto.image)
            .crossfade(500)
            .build(),
        contentScale = ContentScale.FillBounds,
        contentDescription = null,
        error = placeholder,
        placeholder = placeholder
    )
}

@Composable
private fun SelectedIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = String.EMPTY,
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TextBlock(itemDto: CategoryItemDto) {
    BasicText(modifier = Modifier.basicMarquee(), text = itemDto.text)

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

@Composable
private fun FavIcon(
    itemDto: CategoryItemDto,
    onFavClick: (CategoryItemDto) -> Unit
) {
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

@Preview
@Composable
fun StationListItemPreview() {
    val item = CategoryItemDto(
        id = String.EMPTY,
        url = String.EMPTY,
        subText = "Hello",
        text = "Station NameStation NameStation NameStation Name",
        type = DtoItemType.AUDIO,
        isFavorite = true,
        initials = "HE"
    )
    StationListItem(Modifier.fillMaxWidth(), item, false, false, {}, {})
}