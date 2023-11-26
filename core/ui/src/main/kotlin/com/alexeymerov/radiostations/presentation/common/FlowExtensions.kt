package com.alexeymerov.radiostations.presentation.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
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

inline fun <T> Flow<T>.collectWhenStarted(
    lifecycleOwner: LifecycleOwner,
    crossinline action: suspend (T) -> Unit
): Job {
    return lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            collectLatest {
                action.invoke(it)
            }
        }
    }
}


