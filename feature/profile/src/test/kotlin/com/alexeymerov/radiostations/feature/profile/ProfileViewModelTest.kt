package com.alexeymerov.radiostations.feature.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.get
import androidx.paging.AsyncPagingDataDiffer
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.alexeymerov.radiostations.core.domain.usecase.country.FakeCountryUseCase
import com.alexeymerov.radiostations.core.domain.usecase.profile.FakeProfileUsaCase
import com.alexeymerov.radiostations.core.dto.CountryDto
import com.alexeymerov.radiostations.core.test.TestDiffCallback
import com.alexeymerov.radiostations.core.test.TestUpdateCallback
import com.alexeymerov.radiostations.core.test.createTestBitmap
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel.*
import com.google.common.truth.Truth.*
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var profileUsaCase: FakeProfileUsaCase

    private lateinit var countryUseCase: FakeCountryUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    private val tempFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tempFolder.create()

        profileUsaCase = spyk(FakeProfileUsaCase())

        coEvery { profileUsaCase.saveAvatar(any()) } coAnswers {
            val bitmap = firstArg<Bitmap>()
            val file = getAvatarFile()

            file
                .outputStream()
                .use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                    output.flush()
                }

            profileUsaCase.validUserDto = profileUsaCase.validUserDto.copy(avatarFile = file)
            profileUsaCase.userDataFlow.emit(profileUsaCase.validUserDto)
        }

        coEvery { profileUsaCase.deleteAvatar() } coAnswers {
            getAvatarFile().delete()

            profileUsaCase.validUserDto = profileUsaCase.validUserDto.copy(avatarFile = null)
            profileUsaCase.userDataFlow.emit(profileUsaCase.validUserDto)
        }

        countryUseCase = FakeCountryUseCase()

        viewModel = ProfileViewModel(profileUsaCase, countryUseCase, testDispatcher)
    }

    private fun getAvatarFile(): File = try {
        tempFolder.newFile("avatar.jpg")
    } catch (e: Exception) {
        File(tempFolder.root, "avatar.jpg")
    }

    @After
    fun teardown() {
        tempFolder.delete()
        testDispatcher.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun getState_returnsLoadedAsFirstState() = runTest {
        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.Loaded::class.java)
        }
    }

    @Test
    fun getState_afterEnterEditAction_returnsEditState() = runTest {
        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.Loaded::class.java)
            viewModel.setAction(ViewAction.EnterEditMode)
            assertThat(awaitItem()).isInstanceOf(ViewState.InEdit::class.java)
        }
    }

    @Test
    fun getState_afterExitEditAction_returnsLoadedState() = runTest {
        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.Loaded::class.java)
            viewModel.setAction(ViewAction.EnterEditMode)
            assertThat(awaitItem()).isInstanceOf(ViewState.InEdit::class.java)
            viewModel.setAction(ViewAction.SaveEditsAndExitMode)
            assertThat(awaitItem()).isInstanceOf(ViewState.Loaded::class.java)
        }
    }

    @Test
    fun onAction_NewName_updatesOldValue() = runTest {
        val newValue = "NewName"
        viewModel.setAction(ViewAction.EnterEditMode)
        viewModel.viewState.test {
            val userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.name.text).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.NewName(newValue))

            val updatedDto = (awaitItem() as ViewState.InEdit).data
            assertThat(updatedDto.name.text).isEqualTo(newValue)
        }
    }

    @Test
    fun onAction_NewEmail_updatesOldValue() = runTest {
        val newValue = "new@email.com"
        viewModel.setAction(ViewAction.EnterEditMode)
        viewModel.viewState.test {
            val userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.email.text).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.NewEmail(newValue))

            val updatedDto = (awaitItem() as ViewState.InEdit).data
            assertThat(updatedDto.email.text).isEqualTo(newValue)
        }
    }

    @Test
    fun onAction_NewPhone_updatesOldValue() = runTest {
        val newValue = "987"
        viewModel.setAction(ViewAction.EnterEditMode)
        viewModel.viewState.test {
            val userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.phoneNumber.text).isNotEqualTo(newValue)

            viewModel.setAction(ViewAction.NewPhone(newValue))

            val updatedDto = (awaitItem() as ViewState.InEdit).data
            assertThat(updatedDto.phoneNumber.text).isEqualTo(newValue)
        }
    }

    @Test
    fun onAction_NewCountry_updatesOldValue() = runTest {
        val newPhoneCode = 8
        val newValue = FakeCountryUseCase.validCountryDto.copy(phoneCode = newPhoneCode)
        viewModel.setAction(ViewAction.EnterEditMode)
        viewModel.viewState.test {
            val userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.countryCode).isNotEqualTo(newPhoneCode)

            viewModel.setAction(ViewAction.NewCountry(newValue))

            val updatedDto = (awaitItem() as ViewState.InEdit).data
            assertThat(updatedDto.countryCode).isEqualTo(newPhoneCode)
        }
    }

    @Test
    fun countryFlow_inEditMode_returnsFlowWithValidCountries() = runTest {
        viewModel.setAction(ViewAction.EnterEditMode)
        viewModel.viewState.test {
            val pagingData = (awaitItem() as ViewState.InEdit).countryCodes.first()

            val differ = AsyncPagingDataDiffer(TestDiffCallback<CountryDto>(), TestUpdateCallback())
            val job = launch { differ.submitData(pagingData) }
            advanceUntilIdle()

            val items = differ.snapshot().items
            val testItem = items.find { it == FakeCountryUseCase.validCountryDto }

            assertThat(items).isNotEmpty()
            assertThat(testItem).isNotNull()

            job.cancel()
        }
    }

    @Test
    fun onAction_SearchCountry_updatesFlowWithFilteredCountries() = runTest {
        viewModel.setAction(ViewAction.EnterEditMode)
        viewModel.viewState.test {
            val countryCodes = (awaitItem() as ViewState.InEdit).countryCodes

            val pagingData = countryCodes.first()
            var differ = AsyncPagingDataDiffer(TestDiffCallback<CountryDto>(), TestUpdateCallback())
            var job = launch { differ.submitData(pagingData) }
            advanceUntilIdle()

            val items = differ.snapshot().items
            assertThat(items).isNotEmpty()

            job.cancel()

            val searchText = "Eng"
            viewModel.setAction(ViewAction.SearchCountry(searchText))

            val filteredPagingData = countryCodes.first()

            differ = AsyncPagingDataDiffer(TestDiffCallback(), TestUpdateCallback())
            job = launch { differ.submitData(filteredPagingData) }
            advanceUntilIdle()

            val newItems = differ.snapshot().items
            assertThat(newItems).isNotEmpty()
            assertThat(newItems.size).isNotEqualTo(items.size)

            val sameItem = items.find { it.tag == newItems[0].tag }
            assertThat(sameItem).isNotNull()

            assertThat(newItems[0].englishName).contains(searchText)

            job.cancel()
        }
    }

    @Test
    fun onAction_SaveCroppedImage_updatesUserAvatar() = runTest {
        viewModel.setAction(ViewAction.EnterEditMode)

        viewModel.viewState.test {
            val userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.avatarFile).isNull()

            val testBitmap = createTestBitmap()
            viewModel.setAction(ViewAction.SaveCroppedImage(testBitmap))

            val updatedDto = (awaitItem() as ViewState.InEdit).data

            val avatar = updatedDto.avatarFile
            assertThat(avatar).isNotNull()
            avatar!!

            val bitmap = BitmapFactory.decodeFile(avatar.path)
            assertThat(bitmap[1, 1]).isEqualTo(testBitmap[1, 1])
        }
    }

    @Test
    fun onAction_DeleteImage_removesUserAvatar() = runTest {
        viewModel.setAction(ViewAction.EnterEditMode)

        viewModel.viewState.test {
            var userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.avatarFile).isNull()

            val testBitmap = createTestBitmap()
            viewModel.setAction(ViewAction.SaveCroppedImage(testBitmap))

            userDto = (awaitItem() as ViewState.InEdit).data

            val avatar = userDto.avatarFile
            assertThat(avatar).isNotNull()

            viewModel.setAction(ViewAction.DeleteImage)

            userDto = (awaitItem() as ViewState.InEdit).data
            assertThat(userDto.avatarFile).isNull()
        }
    }
}