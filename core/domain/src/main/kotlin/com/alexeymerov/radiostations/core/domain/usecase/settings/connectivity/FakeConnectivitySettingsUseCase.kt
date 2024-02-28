package com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity

import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart


class FakeConnectivitySettingsUseCase : ConnectivitySettingsUseCase {

    private var currentDelay = 0L

    private val currentState = MutableStateFlow(ConnectionStatus.ONLINE)

    override fun getConnectionStatusFlow(): Flow<ConnectionStatus> {
        return currentState.onStart { delay(currentDelay) }
    }

    override suspend fun getConnectionStatus(): ConnectionStatus {
        return currentState.value
    }

    override suspend fun connectionsAllowed(): Boolean {
        return currentState.value == ConnectionStatus.ONLINE
    }

    override suspend fun forceConnectionStatus(status: ConnectionStatus) {
        currentState.value = status
    }

    fun addDelayToFlow(delay: Long) {
        currentDelay = delay
    }
}