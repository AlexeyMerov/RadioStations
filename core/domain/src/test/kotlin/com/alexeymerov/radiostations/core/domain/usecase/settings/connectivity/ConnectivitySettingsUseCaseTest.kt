package com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity

import com.alexeymerov.radiostations.core.datastore.FakeSettingsStore
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase.ConnectionStatus
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConnectivitySettingsUseCaseTest {

    private lateinit var useCase: ConnectivitySettingsUseCase
    private lateinit var settingsStore: SettingsStore

    @Before
    fun setup() {
        settingsStore = spyk(FakeSettingsStore())
        useCase = ConnectivitySettingsUseCaseImpl(settingsStore)
    }

    @Test
    fun `get connection status is return current value`() = runTest {
        val connectionStatus = useCase.getConnectionStatus()

        assertThat(connectionStatus).isAnyOf(ConnectionStatus.ONLINE, ConnectionStatus.OFFLINE)
    }

    @Test
    fun `get connection status if online return online value`() = runTest {
        testConnectionIsOnline()

        val connectionStatus = useCase.getConnectionStatus()

        assertThat(connectionStatus).isEqualTo(ConnectionStatus.ONLINE)
    }

    @Test
    fun `get connection status if offline return online value`() = runTest {
        testConnectionIsOffline()

        val connectionStatus = useCase.getConnectionStatus()

        assertThat(connectionStatus).isEqualTo(ConnectionStatus.OFFLINE)
    }

    @Test
    fun `connection status updates if status changed`() = runTest {
        testConnectionIsOnline()

        val connectionStatus = useCase.getConnectionStatus()
        assertThat(connectionStatus).isEqualTo(ConnectionStatus.ONLINE)

        testConnectionIsOffline()

        assertThat(useCase.getConnectionStatus()).isEqualTo(ConnectionStatus.OFFLINE)
    }

    @Test
    fun `get connection status flow is return current value`() = runTest {
        val connectionStatus = useCase.getConnectionStatusFlow()

        assertThat(connectionStatus.first()).isAnyOf(ConnectionStatus.ONLINE, ConnectionStatus.OFFLINE)
    }

    @Test
    fun `get connection status flow if online return online value`() = runTest {
        testConnectionIsOnline()

        val connectionStatus = useCase.getConnectionStatusFlow()

        assertThat(connectionStatus.first()).isEqualTo(ConnectionStatus.ONLINE)
    }

    @Test
    fun `get connection status flow if offline return online value`() = runTest {
        testConnectionIsOffline()

        val connectionStatus = useCase.getConnectionStatusFlow()

        assertThat(connectionStatus.first()).isEqualTo(ConnectionStatus.OFFLINE)
    }

    @Test
    fun `connection status flow updates if status changed`() = runTest {
        testConnectionIsOnline()

        val connectionStatus = useCase.getConnectionStatusFlow()
        assertThat(connectionStatus.first()).isEqualTo(ConnectionStatus.ONLINE)

        testConnectionIsOffline()

        assertThat(useCase.getConnectionStatusFlow().first()).isEqualTo(ConnectionStatus.OFFLINE)
    }

    @Test
    fun `connectionsAllowed true if online`() = runTest {
        testConnectionIsOnline()

        val connectionsAllowed = useCase.connectionsAllowed()
        assertThat(connectionsAllowed).isTrue()
    }

    @Test
    fun `connectionsAllowed false if offline`() = runTest {
        testConnectionIsOffline()

        val connectionsAllowed = useCase.connectionsAllowed()
        assertThat(connectionsAllowed).isFalse()
    }

    @Test
    fun `connectionsAllowed returns different value if status changed`() = runTest {
        testConnectionIsOffline()

        val connectionsAllowed = useCase.connectionsAllowed()
        assertThat(connectionsAllowed).isFalse()

        testConnectionIsOnline()

        assertThat(useCase.connectionsAllowed()).isTrue()
    }

    @Test
    fun `forceConnection saved given value`() = runTest {
        val connection = useCase.getConnectionStatus()
        assertThat(connection).isEqualTo(ConnectionStatus.ONLINE)

        useCase.forceConnectionStatus(ConnectionStatus.OFFLINE)

        val newValue = useCase.getConnectionStatus()
        assertThat(newValue).isEqualTo(ConnectionStatus.OFFLINE)
    }

    private fun testConnectionIsOnline() {
        every { settingsStore.getIntPrefsFlow(any(), any()) } returns flowOf(ConnectionStatus.ONLINE.value)
    }

    private fun testConnectionIsOffline() {
        every { settingsStore.getIntPrefsFlow(any(), any()) } returns flowOf(ConnectionStatus.OFFLINE.value)
    }
}