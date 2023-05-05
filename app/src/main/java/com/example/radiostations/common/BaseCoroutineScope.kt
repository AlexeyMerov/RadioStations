package com.example.radiostations.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Basic coroutineScope realization for non-lifecycle cases.
 *
 * IMPORTANT part is to call cancelJobs to avoid leaks.
 * */
abstract class BaseCoroutineScope(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CoroutineScope, Cancelable {

    private val supervisorJob = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.e(throwable, "Class: ${this::class.java.simpleName}\nCoroutineContext: $coroutineContext")
    }

    private val coroutineName = CoroutineName(this::class.java.simpleName)

    override val coroutineContext: CoroutineContext
        get() = dispatcher + supervisorJob + exceptionHandler + coroutineName

    override fun cancelJobs() = supervisorJob.cancel()
}

interface Cancelable {
    fun cancelJobs()
}