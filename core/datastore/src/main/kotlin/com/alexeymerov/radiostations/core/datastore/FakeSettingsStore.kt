package com.alexeymerov.radiostations.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeSettingsStore @Inject constructor() : SettingsStore {

    private val stringMap = mutableMapOf<String, String>()
    private val intMap = mutableMapOf<String, Int>()
    private val boolMap = mutableMapOf<String, Boolean>()

    override fun getStringPrefsFlow(key: String, defValue: String): Flow<String> {
        return flow {
            emit(stringMap.getOrDefault(key, defValue))
        }
    }

    override fun getIntPrefsFlow(key: String, defValue: Int): Flow<Int> {
        return flow {
            emit(intMap.getOrDefault(key, defValue))
        }
    }

    override fun getBoolPrefsFlow(key: String, defValue: Boolean): Flow<Boolean> {
        return flow {
            emit(boolMap.getOrDefault(key, defValue))
        }
    }

    override suspend fun setStringPrefs(key: String, value: String) {
        stringMap[key] = value
    }

    override suspend fun setIntPrefs(key: String, value: Int) {
        intMap[key] = value
    }

    override suspend fun setBoolPrefs(key: String, value: Boolean) {
        boolMap[key] = value
    }
}