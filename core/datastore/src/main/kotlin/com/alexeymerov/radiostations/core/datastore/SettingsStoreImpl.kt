package com.alexeymerov.radiostations.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * DataStore works only with simple types so i tried to avoid generics.
 * */
class SettingsStoreImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : SettingsStore {

    override fun getStringPrefsFlow(key: String, defValue: String): Flow<String> = dataStore.data
        .map { it.getOrDefault(stringPreferencesKey(key), defValue) }

    override fun getIntPrefsFlow(key: String, defValue: Int): Flow<Int> = dataStore.data
        .map { it.getOrDefault(intPreferencesKey(key), defValue) }

    override fun getBoolPrefsFlow(key: String, defValue: Boolean): Flow<Boolean> = dataStore.data
        .map { it.getOrDefault(booleanPreferencesKey(key), defValue) }

    override suspend fun setStringPrefs(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun setIntPrefs(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    override suspend fun setBoolPrefs(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    private fun <T> Preferences.getOrDefault(key: Preferences.Key<T>, defValue: T): T {
        return try {
            get(key) ?: defValue
        } catch (e: NoSuchElementException) {
            defValue
        }
    }
}