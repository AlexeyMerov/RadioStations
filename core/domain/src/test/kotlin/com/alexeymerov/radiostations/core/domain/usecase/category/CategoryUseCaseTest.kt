package com.alexeymerov.radiostations.core.domain.usecase.category

import com.alexeymerov.radiostations.core.connectivity.ConnectionMonitor
import com.alexeymerov.radiostations.core.data.repository.category.FakeCategoryRepository
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapperImpl
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.google.common.truth.Truth.*
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CategoryUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var useCase: CategoryUseCase

    @MockK
    private lateinit var connectivitySettings: ConnectivitySettingsUseCase

    @MockK
    private lateinit var connectionMonitor: ConnectionMonitor

    @Before
    fun setup() {
        coEvery { connectivitySettings.connectionsAllowed() } returns true
        coEvery { connectionMonitor.connectionStatusFlow } returns MutableStateFlow(true)

        useCase = CategoryUseCaseImpl(
            FakeCategoryRepository(),
            DtoCategoriesMapperImpl(),
            connectivitySettings,
            connectionMonitor
        )
    }

    @Test
    fun `get categories if no saved data returns empty list`() = runTest {
        val dto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(dto.items).isEmpty()
        assertThat(dto.isError).isFalse()
    }

    @Test
    fun `get categories with invalid url returns empty list`() = runTest {
        val dto = useCase.getAllByUrl("").first()
        assertThat(dto.items).isEmpty()
        assertThat(dto.isError).isFalse()
    }

    @Test
    fun `load categories with valid url updates saved data`() = runTest {
        val dto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(dto.items).isEmpty()
        assertThat(dto.isError).isFalse()

        useCase.loadCategoriesByUrl(FakeCategoryRepository.VALID_URL)

        val newDto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(newDto.items).isNotEmpty()
        assertThat(dto.isError).isFalse()
    }

    @Test
    fun `load categories with error url return error`() = runTest {
        useCase.loadCategoriesByUrl(FakeCategoryRepository.ERROR_URL)
        val dto = useCase.getAllByUrl(FakeCategoryRepository.ERROR_URL).first()
        assertThat(dto.isError).isTrue()
    }

    @Test
    fun `load categories with invalid url do nothing`() = runTest {
        val dto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        val initItems = dto.items

        assertThat(dto.isError).isFalse()

        useCase.loadCategoriesByUrl("")

        val newDto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        val updatedItems = newDto.items

        assertThat(newDto.isError).isFalse()

        assertThat(initItems).containsExactlyElementsIn(updatedItems)
    }

    @Test
    fun `load categories with valid url and connections not allowed returns empty list`() = runTest {
        coEvery { connectivitySettings.connectionsAllowed() } returns false

        val dto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(dto.items).isEmpty()

        useCase.loadCategoriesByUrl(FakeCategoryRepository.VALID_URL)

        val newDto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(newDto.items).isEmpty()
    }

    @Test
    fun `load categories with valid url and no internet returns empty list`() = runTest {
        coEvery { connectionMonitor.connectionStatusFlow } returns MutableStateFlow(false)

        val dto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(dto.items).isEmpty()

        useCase.loadCategoriesByUrl(FakeCategoryRepository.VALID_URL)

        val newDto = useCase.getAllByUrl(FakeCategoryRepository.VALID_URL).first()
        assertThat(newDto.items).isEmpty()
    }

}