package com.alexeymerov.radiostations.feature.settings.connectivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.settings.SettingsViewModel

@Composable
internal fun ConnectivitySettings(
    connectionStatus: ConnectivitySettingsUseCase.ConnectionStatus,
    onAction: (SettingsViewModel.ViewAction) -> Unit
) {
    val isOnline = connectionStatus == ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(text = "Request new items")

        Switch(
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
