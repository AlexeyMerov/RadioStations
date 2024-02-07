package com.alexeymerov.radiostations.core.connectivity.di


import android.content.Context
import android.net.ConnectivityManager
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
object ConnectivityModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Monitors {

        @Binds
        @Singleton
        abstract fun bindConnectionMonitor(monitor: ConnectionMonitorImpl): ConnectionMonitor

    }

}