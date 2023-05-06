package com.alexeymerov.radiostations.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * All extensions have one purpose, shrink code to one line.
 * */

inline fun <T> Flow<T>.collectWhenResumed(
    lifecycleOwner: LifecycleOwner,
    crossinline action: suspend (T) -> Unit
): Job {
    return lifecycleOwner.lifecycleScope.launchWhenResumed {
        collectLatest {
            action.invoke(it)
        }
    }
}

fun <T> Channel<T>.send(coroutineScope: CoroutineScope, data: T) {
    coroutineScope.launch(coroutineScope.coroutineContext) {
        send(data)
    }
}