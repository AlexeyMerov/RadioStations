package com.alexeymerov.radiostations.core.datastore

import android.content.Context
import com.alexeymerov.radiostations.core.test.AndroidTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class SettingsStoreTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @AndroidTest
    lateinit var context: Context

    @Inject
    @AndroidTest
    lateinit var coroutineScope: TestScope

    @Inject
    lateinit var settingsStore: SettingsStore

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun teardown() {
        // Android test because...
        // If using robolectric or junit.rules.TemporaryFolder
        // and call 'clear' or twice edit in one test it may throw the following.
        // [
        // -- Unable to rename ..\datastore\test_user_prefs.preferences_pb.tmp.
        // -- This likely means that there are multiple instances of DataStore for this file.
        // -- Ensure that you are only creating a single instance of datastore for this file.
        // ]
        // Please follow:
        // https://github.com/android/codelab-android-datastore/issues/48
        // https://github.com/robolectric/robolectric/issues/7919
        // https://issuetracker.google.com/issues/203087070

        File(context.filesDir, "datastore").deleteRecursively()
        coroutineScope.cancel()
    }

    @Test
    fun getStringFlow_WithWrongKey_returnsDefaultValue() = coroutineScope.runTest {
        val defValue = "some data"

        val prefsFlow = settingsStore.getStringPrefsFlow("", defValue)
        assertThat(prefsFlow.first()).isEqualTo(defValue)
    }

    @Test
    fun getStringFlow_withValidKeyAndSavedData_returnsValidData() = coroutineScope.runTest {
        val key = "Some key"
        val valueToSave = "Some data"

        settingsStore.setStringPrefs(key, valueToSave)

        val stringPrefsFlow = settingsStore.getStringPrefsFlow(key, "")
        assertThat(stringPrefsFlow.first()).isEqualTo(valueToSave)
    }

    @Test
    fun saveString_isSaving() = coroutineScope.runTest {
        val key = "Some key"
        val valueToSave = "Some data"
        val defValue = "defVal"

        val prefsFlow = settingsStore.getStringPrefsFlow(key, defValue)
        assertThat(prefsFlow.first()).isEqualTo(defValue)

        settingsStore.setStringPrefs(key, valueToSave)
        assertThat(prefsFlow.first()).isEqualTo(valueToSave)
    }


    @Test
    fun saveNewString_withSameKey_updatesOldValue() = coroutineScope.runTest {
        val key = "Some key"
        val oldData = "Some data"
        val newData = "Some new data"

        settingsStore.setStringPrefs(key, oldData)

        val prefsFlow = settingsStore.getStringPrefsFlow(key, "")
        assertThat(prefsFlow.first()).isEqualTo(oldData)

        settingsStore.setStringPrefs(key, newData)
        assertThat(prefsFlow.first()).isEqualTo(newData)
    }

    // -------------------------------------------

    @Test
    fun getIntFlow_WithWrongKey_returnsDefaultValue() = coroutineScope.runTest {
        val defValue = 123

        val prefsFlow = settingsStore.getIntPrefsFlow("", defValue)
        assertThat(prefsFlow.first()).isEqualTo(defValue)
    }

    @Test
    fun getIntFlow_withValidKeyAndSavedData_returnsValidData() = coroutineScope.runTest {
        val key = "Some key"
        val valueToSave = 111

        settingsStore.setIntPrefs(key, valueToSave)

        val prefsFlow = settingsStore.getIntPrefsFlow(key, 0)
        assertThat(prefsFlow.first()).isEqualTo(valueToSave)
    }

    @Test
    fun saveInt_isSaving() = coroutineScope.runTest {
        val key = "Some key"
        val valueToSave = 999
        val defValue = 123

        val prefsFlow = settingsStore.getIntPrefsFlow(key, defValue)
        assertThat(prefsFlow.first()).isEqualTo(defValue)

        settingsStore.setIntPrefs(key, valueToSave)
        assertThat(prefsFlow.first()).isEqualTo(valueToSave)
    }


    @Test
    fun saveNewInt_withSameKey_updatesOldValue() = coroutineScope.runTest {
        val key = "Some key"
        val oldData = 555
        val newData = 777

        settingsStore.setIntPrefs(key, oldData)

        val prefsFlow = settingsStore.getIntPrefsFlow(key, 0)
        assertThat(prefsFlow.first()).isEqualTo(oldData)

        settingsStore.setIntPrefs(key, newData)
        assertThat(prefsFlow.first()).isEqualTo(newData)
    }

    // -------------------------------------------

    @Test
    fun getBoolFlow_WithWrongKey_returnsDefaultValue() = coroutineScope.runTest {
        val defValue = false

        val prefsFlow = settingsStore.getBoolPrefsFlow("", defValue)
        assertThat(prefsFlow.first()).isEqualTo(defValue)
    }

    @Test
    fun getBoolFlow_withValidKeyAndSavedData_returnsValidData() = coroutineScope.runTest {
        val key = "Some key"
        val valueToSave = true

        settingsStore.setBoolPrefs(key, valueToSave)

        val prefsFlow = settingsStore.getBoolPrefsFlow(key, false)
        assertThat(prefsFlow.first()).isEqualTo(valueToSave)
    }

    @Test
    fun saveBool_isSaving() = coroutineScope.runTest {
        val key = "Some key"
        val valueToSave = true
        val defValue = false

        val prefsFlow = settingsStore.getBoolPrefsFlow(key, defValue)
        assertThat(prefsFlow.first()).isEqualTo(defValue)

        settingsStore.setBoolPrefs(key, valueToSave)
        assertThat(prefsFlow.first()).isEqualTo(valueToSave)
    }


    @Test
    fun saveNewBool_withSameKey_updatesOldValue() = coroutineScope.runTest {
        val key = "Some key"
        val oldData = true
        val newData = false

        settingsStore.setBoolPrefs(key, oldData)

        val prefsFlow = settingsStore.getBoolPrefsFlow(key, false)
        assertThat(prefsFlow.first()).isEqualTo(oldData)

        settingsStore.setBoolPrefs(key, newData)
        assertThat(prefsFlow.first()).isEqualTo(newData)
    }

}