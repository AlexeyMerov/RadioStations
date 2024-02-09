package com.alexeymerov.radiostations.core.datastore.di

import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.datastore.SettingsStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingStoreModule {

    @Binds
    @Singleton
    abstract fun bindSettingsStore(store: SettingsStoreImpl): SettingsStore

}