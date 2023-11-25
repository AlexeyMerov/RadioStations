package com.alexeymerov.radiostations.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface SettingsStore {

    fun getStringPrefsFlow(key: String, defValue: String): Flow<String>

    fun getIntPrefsFlow(key: String, defValue: Int): Flow<Int>

    fun getBoolPrefsFlow(key: String, defValue: Boolean): Flow<Boolean>

    suspend fun setStringPrefs(key: String, value: String)

    suspend fun setIntPrefs(key: String, value: Int)

    suspend fun setBoolPrefs(key: String, value: Boolean)
}