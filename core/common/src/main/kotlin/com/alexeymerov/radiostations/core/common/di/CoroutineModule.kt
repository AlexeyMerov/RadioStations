package com.alexeymerov.radiostations.core.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

    @Singleton
    @Provides
    @Dispatcher(RadioDispatchers.IO)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    @Dispatcher(RadioDispatchers.MAIN)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Singleton
    @Provides
    @Dispatcher(RadioDispatchers.DEFAULT)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.e(throwable, "CoroutineContext: $coroutineContext")
    }

    @Singleton
    @Provides
    fun provideCoroutineContext(
        @Dispatcher(RadioDispatchers.IO) dispatcher: CoroutineDispatcher,
        exceptionHandler: CoroutineExceptionHandler
    ): CoroutineContext = SupervisorJob() + dispatcher + exceptionHandler

    @Singleton
    @Provides
    @AppScope
    fun provideCoroutineScope(coroutineContext: CoroutineContext): CoroutineScope = CoroutineScope(coroutineContext)

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: RadioDispatchers)
enum class RadioDispatchers { DEFAULT, IO, MAIN }


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class AppScope