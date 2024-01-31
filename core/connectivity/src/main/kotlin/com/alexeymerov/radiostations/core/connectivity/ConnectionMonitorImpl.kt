package com.alexeymerov.radiostations.core.connectivity

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class ConnectionMonitorImpl @Inject constructor(
    connectivityManager: ConnectivityManager,
    networkRequest: NetworkRequest
) : ConnectionMonitor {

    @SuppressLint("MissingPermission")
    private val _connectionStatusFlow = MutableStateFlow(connectivityManager.activeNetwork != null)
    override val connectionStatusFlow = _connectionStatusFlow.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Timber.d("networkCallback onAvailable")
            _connectionStatusFlow.value = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Timber.d("networkCallback onLost")
            _connectionStatusFlow.value = false
        }
    }

    init {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

}