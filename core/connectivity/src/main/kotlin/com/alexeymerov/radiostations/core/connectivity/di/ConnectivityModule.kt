package com.alexeymerov.radiostations.core.connectivity.di


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.alexeymerov.radiostations.core.connectivity.ConnectionMonitor
import com.alexeymerov.radiostations.core.connectivity.ConnectionMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [ConnectivityModule.Monitors::class])
@InstallIn(SingletonComponent::class)
class ConnectivityModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Monitors {

        @Binds
        @Singleton
        abstract fun bindConnectionMonitor(monitor: ConnectionMonitorImpl): ConnectionMonitor

    }

}