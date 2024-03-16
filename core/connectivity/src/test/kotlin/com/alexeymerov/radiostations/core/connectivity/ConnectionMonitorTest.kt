package com.alexeymerov.radiostations.core.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
class ConnectionMonitorTest {

    private lateinit var connectionMonitor: ConnectionMonitor
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities

    @Before
    fun setup() {
        connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCapabilities = ShadowNetworkCapabilities.newInstance()
    }

    @Test
    fun `if exist active network return true state`() = runTest {
        shadowOf(connectivityManager).setDefaultNetworkActive(true)

        connectionMonitor = ConnectionMonitorImpl(connectivityManager)

        val networkState = connectionMonitor.connectionStatusFlow.first()
        assertThat(networkState).isTrue()
    }

    @Test
    fun `if no active network return false state`() = runTest {
        shadowOf(connectivityManager).setDefaultNetworkActive(false)

        connectionMonitor = ConnectionMonitorImpl(connectivityManager)

        val networkState = connectionMonitor.connectionStatusFlow.first()
        assertThat(networkState).isFalse()
    }

}