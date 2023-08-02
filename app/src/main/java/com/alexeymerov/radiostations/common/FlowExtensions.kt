package com.alexeymerov.radiostations.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


/**
 * All extensions have one purpose, shrink code to one line.
 * */

fun <T> Channel<T>.send(coroutineScope: CoroutineScope, data: T) {
    coroutineScope.launch(coroutineScope.coroutineContext) {
        send(data)
    }
}

fun <T> MutableSharedFlow<T>.emit(coroutineScope: CoroutineScope, data: T) {
    coroutineScope.launch(coroutineScope.coroutineContext) {
        emit(data)
    }
}