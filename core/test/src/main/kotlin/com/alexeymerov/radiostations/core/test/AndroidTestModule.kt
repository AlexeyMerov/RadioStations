package com.alexeymerov.radiostations.core.test

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@AndroidTest
class AndroidTestModule {

    @Singleton
    @Provides
    @AndroidTest
    fun provideTestContext(): Context = ApplicationProvider.getApplicationContext()

    @Singleton
    @Provides
    @AndroidTest
    fun provideTestScope(): TestScope = TestScope(UnconfinedTestDispatcher())

    @Provides
    @Singleton
    @AndroidTest
    fun provideCoroutineDispatchers(): CoroutineDispatcher = UnconfinedTestDispatcher()

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AndroidTest