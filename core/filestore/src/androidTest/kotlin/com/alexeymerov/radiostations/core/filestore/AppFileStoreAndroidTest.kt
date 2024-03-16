package com.alexeymerov.radiostations.core.filestore

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.net.toUri
import com.alexeymerov.radiostations.core.test.AndroidTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class AppFileStoreAndroidTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @AndroidTest
    lateinit var context: Context

    private lateinit var appFileStore: AppFileStore

    @Before
    fun setup() {
        hiltRule.inject()
        appFileStore = AppFileStoreImpl(context)
    }

    @Test
    fun getFile_withName_returnsValidFileName() = runTest {
        val fileName = "TestFileName.jpg"
        val file = appFileStore.getFileByName(fileName)!!

        assertThat(file.name).contains(fileName)

        file.delete()
    }

    @Test
    fun getFile_withEmptyName_returnsParentFolderOrNull() = runTest {
        val fileName = ""
        val file = appFileStore.getFileByName(fileName)

        val name = file?.name
        if (name != null) {
            assertThat(name).isEqualTo("files")
        } else {
            assertThat(file).isNull()
        }
    }

    @Test
    fun getFile_whenNoExist_returnsZeroLength() = runTest {
        val fileName = "jj.jpg"
        val file = appFileStore.getFileByName(fileName)!!

        assertThat(file.length()).isEqualTo(0)

        file.delete()
    }

    @Test
    fun getFile_whenExist_returnsValidFile() = runTest {
        val fileName = "TestFileName.jpg"

        writeSomeDataToFile(fileName)

        val file = appFileStore.getFileByName(fileName)!!

        assertThat(file.length()).isGreaterThan(0L)
        assertThat(file.canRead()).isTrue()

        file.delete()
    }

    @Test
    fun getTempFile_withValidNameAndExt_returnsValidUri() = runTest {
        val fileName = "TestFileName"

        val fileUri = appFileStore.getTempUri(fileName)
        val uriPath = fileUri.path

        assertThat(uriPath).isNotNull()
        uriPath!!

        val file = File(uriPath)

        assertThat(file.name).contains(fileName)

        file.delete()
    }

    @Test
    fun getTempFile_withEmptyName_throwException() = runTest {
        try {
            appFileStore.getTempUri("")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Test
    fun removeFileByUri_removeFileFromStorage() = runTest {
        val fileName = "TestFileName.jpg"

        var file = appFileStore.getFileByName(fileName)!!
        assertThat(file.length()).isEqualTo(0)

        writeSomeDataToFile(fileName)

        file = appFileStore.getFileByName(fileName)!!
        assertThat(file.length()).isGreaterThan(0L)

        appFileStore.removeFileByUri(file.toUri())

        file = appFileStore.getFileByName(fileName)!!
        assertThat(file.length()).isEqualTo(0)

        file.delete()
    }

    @Test
    fun copyFromBitmap_writesDataToFile() = runTest {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.RED)

        val fileName = "TestFileName.jpg"

        var file = appFileStore.getFileByName(fileName)!!
        assertThat(file.length()).isEqualTo(0)

        appFileStore.copyFromBitmapToFile(bitmap, fileName)

        file = appFileStore.getFileByName(fileName)!!
        assertThat(file.length()).isGreaterThan(0L)

        val savedBitmap = BitmapFactory.decodeFile(file.path)
        val colorAtPosition = savedBitmap.getColor(1, 1)
        assertThat(colorAtPosition.toArgb()).isEqualTo(Color.RED)

        file.delete()
    }

    private fun writeSomeDataToFile(fileName: String) {
        val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        outputStream.use { output ->
            "somedata".byteInputStream().use { input ->
                output.write(input.read())
            }
            output.flush()
        }
    }
}