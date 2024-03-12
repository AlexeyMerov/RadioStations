package com.alexeymerov.radiostations.feature.player.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // -- at the moment only for the service --//

    @Singleton
    @Provides
    fun provideSupervisorJob(): CompletableJob = SupervisorJob()

    @Singleton
    @Provides
    fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.e(throwable, "CoroutineContext: $coroutineContext")
    }

    @Singleton
    @Provides
    fun provideCoroutineContext(
        supervisorJob: CompletableJob,
        dispatcher: CoroutineDispatcher,
        exceptionHandler: CoroutineExceptionHandler
    ): CoroutineContext = supervisorJob + dispatcher + exceptionHandler

    @Singleton
    @Provides
    fun provideCoroutineScope(coroutineContext: CoroutineContext): CoroutineScope = CoroutineScope(coroutineContext)

}