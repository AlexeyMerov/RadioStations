package com.alexeymerov.radiostations.feature.settings

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.view.BasicText

@Composable
internal fun SettingsTabRow(currentTab: SettingTab, onTabClick: (SettingTab) -> Unit) {
    val defaultIndicatorWidth = 32.dp
    val tabWidths = remember { mutableStateMapOf<Int, Dp>() }
    TabRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        selectedTabIndex = currentTab.index,
        divider = { HorizontalDivider(thickness = 0.5.dp) },
        indicator = { tabPositions ->
            Box(
                Modifier
                    .customTabIndicatorOffset(
                        currentTabPosition = tabPositions[currentTab.index],
                        tabWidth = tabWidths.getOrDefault(currentTab.index, defaultIndicatorWidth)
                    )
                    .height(4.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        SettingTab.entries.forEach { tab ->
            CustomTab(
                tab = tab,
                currentTab = currentTab,
                text = stringResource(tab.stringId),
                onTabClick = onTabClick::invoke,
                onWidthMeasured = { textWidth ->
                    if (textWidth > defaultIndicatorWidth) {
                        tabWidths[tab.index] = textWidth
                    }
                }
            )
        }
    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "customTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing), label = ""
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing), label = ""
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

@Composable
internal fun CustomTab(
    tab: SettingTab,
    currentTab: SettingTab,
    text: String,
    onTabClick: (SettingTab) -> Unit,
    onWidthMeasured: (Dp) -> Unit
) {
    Tab(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        selected = tab == currentTab,
        onClick = { onTabClick.invoke(tab) },
        unselectedContentColor = MaterialTheme.colorScheme.outline,
    ) {
        BasicText(
            modifier = Modifier.padding(8.dp),
            text = text,
            onTextSize = {
                onWidthMeasured.invoke(it.width)
            }
        )
    }
}

internal enum class SettingTab(
    val index: Int,
    @StringRes val stringId: Int
) {
    USER(0, R.string.user),
    DEV(1, R.string.dev)
}
