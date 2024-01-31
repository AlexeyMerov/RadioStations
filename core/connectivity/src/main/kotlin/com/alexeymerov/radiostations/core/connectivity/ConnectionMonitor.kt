package com.alexeymerov.radiostations.core.connectivity

import kotlinx.coroutines.flow.StateFlow

interface ConnectionMonitor {

    val connectionStatusFlow: StateFlow<Boolean>

}