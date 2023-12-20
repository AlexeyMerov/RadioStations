package com.alexeymerov.radiostations.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.view.BasicText

@Composable
internal fun TabRow(currentTab: SettingTab, onTabClick: (SettingTab) -> Unit) {
    androidx.compose.material3.TabRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        selectedTabIndex = currentTab.index,
        divider = { Divider(thickness = 0.5.dp) },
        indicator = { tabPositions ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[currentTab.index])
                    .padding(horizontal = 64.dp) // static but consider calculate width of text
                    .height(4.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        CustomTab(
            tab = SettingTab.USER,
            currentTab = currentTab,
            text = stringResource(R.string.user),
            onTabClick = onTabClick::invoke
        )
        CustomTab(
            tab = SettingTab.DEV,
            currentTab = currentTab,
            text = stringResource(R.string.dev),
            onTabClick = onTabClick::invoke
        )
    }
}

@Composable
internal fun CustomTab(
    tab: SettingTab,
    currentTab: SettingTab,
    text: String,
    onTabClick: (SettingTab) -> Unit
) {
    Tab(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        selected = tab == currentTab,
        onClick = { onTabClick.invoke(tab) },
        unselectedContentColor = MaterialTheme.colorScheme.outline,
    ) {
        BasicText(
            modifier = Modifier.padding(8.dp),
            text = text
        )
    }
}

internal enum class SettingTab(val index: Int) {
    USER(0), DEV(1)
}
