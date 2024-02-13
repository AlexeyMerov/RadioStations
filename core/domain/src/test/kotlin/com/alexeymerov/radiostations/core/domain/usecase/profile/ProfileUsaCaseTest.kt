package com.alexeymerov.radiostations.core.domain.usecase.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.get
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alexeymerov.radiostations.core.datastore.FakeSettingsStore
import com.alexeymerov.radiostations.core.filestore.AppFileStore
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class ProfileUsaCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var fileStore: AppFileStore

    private lateinit var useCase: ProfileUsaCase

    private val tempFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        tempFolder.create()

        coEvery { fileStore.getFileByName(any()) } answers {
            val fileName = firstArg<String>()
            File(tempFolder.root, fileName)
        }

        coEvery { fileStore.getTempUri(any()) } answers {
            val prefix = firstArg<String>()
            val suffix = secondArg<AppFileStore.FileSuffix>().value
            tempFolder.newFile("$prefix$suffix").toUri()
        }

        coEvery { fileStore.removeFileByUri(any()) } answers { firstArg<Uri>().toFile().delete() }

        coEvery { fileStore.copyFromBitmapToFile(any(), any()) } answers {
            val bitmap = firstArg<Bitmap>()
            val fileName = secondArg<String>()

            println("bitmap: w ${bitmap.width}, h ${bitmap.height}")

            val file = tempFolder.newFile(fileName)
            file.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.flush()
            }
        }

        useCase = ProfileUsaCaseImpl(FakeSettingsStore(), fileStore)
    }

    @After
    fun teardown() {
        tempFolder.delete()
    }

    @Test
    fun `save avatar saves it to file AND get avatar returns the same file`() = runTest {
        var avatarFile = useCase.getAvatar()!!
        assertThat(avatarFile.length()).isEqualTo(0)

        val bitmap = createBitmap()
        useCase.saveAvatar(bitmap)

        avatarFile = useCase.getAvatar()!!
        assertThat(avatarFile.length()).isGreaterThan(0L)

        val savedBitmap = BitmapFactory.decodeFile(avatarFile.path)
        assertThat(savedBitmap).isInstanceOf(Bitmap::class.java)

        val colorAtPosition = savedBitmap[1, 1]
        val initColorAtPosition = bitmap[1, 1]
        assertThat(colorAtPosition).isEqualTo(initColorAtPosition)
    }

    @Test
    fun `get avatar if not exist return empty file`() = runTest {
        val file = useCase.getAvatar()!!
        assertThat(file.length()).isEqualTo(0)
    }

    @Test
    fun `delete avatar removes file`() = runTest {
        var avatarFile = useCase.getAvatar()!!
        assertThat(avatarFile.length()).isEqualTo(0)

        useCase.saveAvatar(createBitmap())

        avatarFile = useCase.getAvatar()!!
        assertThat(avatarFile.length()).isGreaterThan(0L)

        useCase.deleteAvatar()

        avatarFile = useCase.getAvatar()!!
        assertThat(avatarFile.length()).isEqualTo(0)
    }

    @Test
    fun `get temp uri returns valid uri to file`() = runTest {
        val uri = useCase.getAvatarTempUri()
        val file = uri.toFile()
        assertThat(file.isFile).isTrue()
    }

    private fun createBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.RED)
        return bitmap
    }

    @Test
    fun `get userdata returns valid data`() = runTest {
        val data = useCase.getUserData().first()

        assertThat(data.countryCode).isGreaterThan(0)
        assertThat(data.isEverythingValid).isTrue()

        assertThat(data.name.text).isNotEmpty()
        assertThat(data.name.errorTextResId).isNull()

        assertThat(data.email.text).isNotEmpty()
        assertThat(data.email.errorTextResId).isNull()

        assertThat(data.phoneNumber.text).isNotEmpty()
        assertThat(data.phoneNumber.errorTextResId).isNull()
    }

    @Test
    fun `save userdata updates existed data`() = runTest {
        val dataFlow = useCase.getUserData()
        val data = dataFlow.first()

        val newData = data.copy(
            countryCode = 3,
            name = data.name.copy(text = "Test"),
            email = data.email.copy(text = "Test@test"),
            phoneNumber = data.phoneNumber.copy(text = "23450394")
        )

        assertThat(data.countryCode).isNotEqualTo(newData.countryCode)
        assertThat(data.name.text).isNotEqualTo(newData.name.text)
        assertThat(data.email.text).isNotEqualTo(newData.email.text)
        assertThat(data.phoneNumber.text).isNotEqualTo(newData.phoneNumber.text)

        useCase.saveUserData(newData)

        val updatedData = dataFlow.first()

        assertThat(updatedData.countryCode).isEqualTo(newData.countryCode)
        assertThat(updatedData.name.text).isEqualTo(newData.name.text)
        assertThat(updatedData.email.text).isEqualTo(newData.email.text)
        assertThat(updatedData.phoneNumber.text).isEqualTo(newData.phoneNumber.text)
    }

}