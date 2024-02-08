package com.alexeymerov.radiostations.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_USER_PREFS = "test_user_prefs"

@RunWith(AndroidJUnit4::class)
class SettingsStoreTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val coroutineContext = StandardTestDispatcher()
    private val coroutineScope = TestScope(coroutineContext)

    private lateinit var testDataStore: DataStore<Preferences>

    private lateinit var settingsStore: SettingsStore

    @Before
    fun setup() {
        testDataStore = PreferenceDataStoreFactory.create(
            scope = coroutineScope,
            produceFile = { context.preferencesDataStoreFile(TEST_USER_PREFS) }
        )

        settingsStore = SettingsStoreImpl(testDataStore)
    }

    @After
    fun teardown() {
        // If call 'clear' or twice edit in one test it may throw the following.
        // [
        // -- Unable to rename ..\datastore\test_user_prefs.preferences_pb.tmp.
        // -- This likely means that there are multiple instances of DataStore for this file.
        // -- Ensure that you are only creating a single instance of datastore for this file.
        // ]
        // Please follow the issues:
        // https://github.com/android/codelab-android-datastore/issues/48
        // https://github.com/robolectric/robolectric/issues/7919
        // https://issuetracker.google.com/issues/203087070

        // File(context.filesDir, "datastore").deleteRecursively()
//        testDataStore.edit {
//            it.clear()
//        }
        coroutineScope.cancel()
    }

    @Test
    fun `get string flow with wrong key returns default value`() = runTest(coroutineContext) {
        val defStringValue = "some data"

        val stringPrefsFlow = settingsStore.getStringPrefsFlow("", defStringValue)
        assertThat(stringPrefsFlow.first()).isEqualTo(defStringValue)
    }

    @Test
    fun `get string flow with valid key and saved data returns data string`() = runTest(coroutineContext) {
        val key = "Some key"
        val valueToSave = "Some data"

        settingsStore.setStringPrefs(key, valueToSave)

        val stringPrefsFlow = settingsStore.getStringPrefsFlow(key, "")
        assertThat(stringPrefsFlow.first()).isEqualTo(valueToSave)
    }

    @Test
    fun `save string is saving`() = runTest(coroutineContext) {
        val key = "Some key"
        val valueToSave = "Some data"
        val defValue = "defVal"

        val stringPrefsFlow = settingsStore.getStringPrefsFlow(key, defValue)
        assertThat(stringPrefsFlow.first()).isEqualTo(defValue)

        settingsStore.setStringPrefs(key, valueToSave)
        assertThat(stringPrefsFlow.first()).isEqualTo(valueToSave)
    }

    // read above
//    @Test
//    fun `save new string value with same key updates old value`() = runTest(coroutineContext) {
//        val key = "Some key"
//        val oldData = "Some data"
//        val newData = "Some new data"
//
//        settingsStore.setStringPrefs(key, oldData)
//
//        val stringPrefsFlow = settingsStore.getStringPrefsFlow(key, "")
//        assertThat(stringPrefsFlow.first()).isEqualTo(oldData)
//
//        settingsStore.setStringPrefs(key, newData)
//        assertThat(stringPrefsFlow.first()).isEqualTo(newData)
//    }

}