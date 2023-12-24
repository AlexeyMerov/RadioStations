package com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity

import kotlinx.coroutines.flow.Flow


interface ConnectivitySettingsUseCase {

    fun getConnectionStatusFlow(): Flow<ConnectionStatus>

    suspend fun getConnectionStatus(): ConnectionStatus

    suspend fun allowConnections(): Boolean

    suspend fun forceConnectionStatus(status: ConnectionStatus)

    enum class ConnectionStatus(val value: Int) {
        ONLINE(0), OFFLINE(1)
    }
}