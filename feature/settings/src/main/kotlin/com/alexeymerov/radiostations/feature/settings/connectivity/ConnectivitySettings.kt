package com.alexeymerov.radiostations.feature.settings.connectivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags.CONNECTIVITY_SWITCH
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags.CONNECTIVITY_VIEW
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel

@Composable
internal fun ConnectivitySettings(
    modifier: Modifier,
    connectionStatus: ConnectionStatus,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    Row(
        modifier = modifier
            .height(ButtonDefaults.MinHeight)
            .clip(ButtonDefaults.shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        Color.Transparent
                    )
                )
            )
            .testTag(CONNECTIVITY_VIEW),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            modifier = Modifier.padding(start = 16.dp),
            text = "Request new items",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Switch(
            modifier = Modifier
                .padding(end = 4.dp)
                .testTag(CONNECTIVITY_SWITCH),
            checked = connectionStatus == ConnectionStatus.ONLINE,
            onCheckedChange = { isChecked ->
                val newStatus = if (isChecked) ConnectionStatus.ONLINE else ConnectionStatus.OFFLINE
                onAction.invoke(SettingsViewModel.ViewAction.ChangeConnection(newStatus))
            })
    }
}
