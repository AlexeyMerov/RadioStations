package com.alexeymerov.radiostations.core.connectivity

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class ConnectionMonitorImpl @Inject constructor(
    connectivityManager: ConnectivityManager
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

        override fun onUnavailable() {
            super.onUnavailable()
            Timber.d("networkCallback onUnavailable")
            _connectionStatusFlow.value = false
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Timber.d("networkCallback onCapabilitiesChanged \n[network] $network \n[networkCapabilities] $networkCapabilities ")

            val hasInternetCapability = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            val hasCell = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

            if (!hasInternetCapability || (!hasWifi && !hasCell)) {
                _connectionStatusFlow.value = false
            }
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

}