package com.alexeymerov.radiostations.feature.category.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.view.BasicText
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CategoryListItem(
    modifier: Modifier,
    itemDto: CategoryItemDto,
    onCategoryClick: (CategoryItemDto) -> Unit,
    onRevealAction: () -> Unit,
) {
    RevealSwipe(
        modifier = modifier,
        directions = setOf(RevealDirection.EndToStart),
        backgroundEndActionLabel = null,
        backgroundStartActionLabel = null,
        backgroundCardEndColor = MaterialTheme.colorScheme.surface,
        backgroundCardContentColor = MaterialTheme.colorScheme.onSecondary,
        onBackgroundEndClick = {
            onRevealAction.invoke()
            true
        },
        hiddenContentEnd = {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Rounded.QuestionMark,
                contentDescription = null
            )
        },
        content = {
            Card(
                onClick = { onCategoryClick.invoke(itemDto) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        modifier = Modifier.padding(16.dp),
                        text = itemDto.text
                    )
                }
            }
        }
    )
}