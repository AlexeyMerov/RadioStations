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
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel

@Composable
internal fun ConnectivitySettings(
    modifier: Modifier,
    connectionStatus: ConnectivitySettingsUseCase.ConnectionStatus,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    val isOnline = connectionStatus == ConnectivitySettingsUseCase.ConnectionStatus.ONLINE


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
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            modifier = Modifier.padding(start = 16.dp),
            text = "Request new items",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Switch(
            modifier = Modifier.padding(end = 4.dp),
            checked = isOnline,
            onCheckedChange = {
                onAction.invoke(
                    SettingsViewModel.ViewAction.ChangeConnection(
                        if (isOnline) ConnectivitySettingsUseCase.ConnectionStatus.OFFLINE else ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                    )
                )
            })
    }
}
