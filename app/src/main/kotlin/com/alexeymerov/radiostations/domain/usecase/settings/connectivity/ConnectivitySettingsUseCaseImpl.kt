package com.alexeymerov.radiostations.domain.usecase.settings.connectivity

import androidx.datastore.preferences.core.intPreferencesKey
import com.alexeymerov.radiostations.data.local.datastore.SettingsStore
import com.alexeymerov.radiostations.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConnectivitySettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore
) : ConnectivitySettingsUseCase {

    override fun getConnectionStatusFlow(): Flow<ConnectionStatus> {
        return settingsStore
            .getIntPrefsFlow(CONNECTION_KEY, defValue = 0)
            .map { prefValue -> ConnectionStatus.values().first { it.value == prefValue } }
    }

    override suspend fun getConnectionStatus(): ConnectionStatus {
        return getConnectionStatusFlow().first()
    }

    override suspend fun isOnline(): Boolean {
        return getConnectionStatus() == ConnectionStatus.ONLINE
    }

    override suspend fun setConnectionStatus(status: ConnectionStatus) {
        settingsStore.satPrefs(CONNECTION_KEY, status.value)
    }

    companion object {
        val CONNECTION_KEY = intPreferencesKey("connection")
    }
}