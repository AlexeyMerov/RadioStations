package com.alexeymerov.radiostations.feature.category.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.ui.extensions.ignoreWidthConstrains
import com.alexeymerov.radiostations.core.ui.view.BasicText

@Composable
internal fun HeaderListItem(modifier: Modifier, itemDto: CategoryItemDto, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .ignoreWidthConstrains(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .clickable { onClick.invoke() }

    ) {
        BasicText(
            text = itemDto.text,
            textStyle = MaterialTheme.typography.titleSmall
        )

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary),
        ) {
            BasicText(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = itemDto.subItemsCount.toString(),
                textStyle = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}