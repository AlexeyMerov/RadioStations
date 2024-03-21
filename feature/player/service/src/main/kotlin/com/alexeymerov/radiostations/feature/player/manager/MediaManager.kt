package com.alexeymerov.radiostations.feature.player.manager

interface MediaManager {

    fun setupPlayer()

    fun play()

    fun stop()

    fun onDestroy()

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    companion object {
        const val DYNAMIC_SHORTCUT_ID = "latest_station_static_id"
    }

    interface Listener {
        fun onControllerInitialized()
    }

}