package com.alexeymerov.radiostations.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.alexeymerov.radiostations.core.test.AndroidTest
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.test.TestScope
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataStoreModule::class])
object TestDataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @AndroidTest context: Context,
        @AndroidTest coroutineScope: TestScope
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = coroutineScope,
            produceFile = { context.preferencesDataStoreFile("test_user_prefs") }
        )
    }

}