package com.alexeymerov.radiostations.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.activity.ComponentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_NAME, ComponentActivity.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideAppResources(@ApplicationContext context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun provideSupervisorJob(): CompletableJob = SupervisorJob()


    // -- at the moment only for one service --//

    @Singleton
    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

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

    // -- just for one service at the time --//

    private companion object {
        const val SHARED_NAME = "shared_radio"
    }
}