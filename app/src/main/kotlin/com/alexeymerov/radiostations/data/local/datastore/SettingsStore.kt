package com.alexeymerov.radiostations.data.local.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface SettingsStore {

    fun getStringPrefsFlow(key: Preferences.Key<String>): Flow<String>

    fun getIntPrefsFlow(key: Preferences.Key<Int>): Flow<Int>

    suspend fun satPrefs(key: Preferences.Key<String>, value: String)

    suspend fun satPrefs(key: Preferences.Key<Int>, value: Int)
}