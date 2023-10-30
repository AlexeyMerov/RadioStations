package com.alexeymerov.radiostations.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsStoreImpl @Inject constructor(private val userPrefs: DataStore<Preferences>) : SettingsStore {

    override fun getStringPrefsFlow(key: Preferences.Key<String>, defValue: String): Flow<String> = userPrefs.data.map { it[key] ?: defValue }

    override fun getIntPrefsFlow(key: Preferences.Key<Int>, defValue: Int): Flow<Int> = userPrefs.data.map { it[key] ?: defValue }

    override fun getBoolPrefsFlow(key: Preferences.Key<Boolean>, defValue: Boolean): Flow<Boolean> = userPrefs.data.map { it[key] ?: defValue }

    override suspend fun satPrefs(key: Preferences.Key<String>, value: String) {
        userPrefs.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun satPrefs(key: Preferences.Key<Int>, value: Int) {
        userPrefs.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun satPrefs(key: Preferences.Key<Boolean>, value: Boolean) {
        userPrefs.edit { preferences ->
            preferences[key] = value
        }
    }
}