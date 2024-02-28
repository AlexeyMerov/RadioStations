package com.alexeymerov.radiostations.feature.category.item

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.view.BasicText

@Composable
internal fun SubCategoryListItem(modifier: Modifier, itemDto: CategoryItemDto, onCategoryClick: (CategoryItemDto) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .clip(CardDefaults.shape) // for ripple bounds
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CardDefaults.shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colorScheme.onBackground),
                onClick = { onCategoryClick.invoke(itemDto) }
            ), // we need to duplicate paddings on each item for unbounded selection here
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            modifier = Modifier.padding(16.dp),
            text = itemDto.text,
            textStyle = MaterialTheme.typography.titleSmall
        )

        Icon(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp),
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null
        )
    }
}