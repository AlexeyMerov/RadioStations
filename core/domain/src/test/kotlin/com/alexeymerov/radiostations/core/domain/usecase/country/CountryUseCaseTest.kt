package com.alexeymerov.radiostations.core.domain.usecase.country

import androidx.paging.AsyncPagingDataDiffer
import com.alexeymerov.radiostations.core.data.repository.country.CountryRepository
import com.alexeymerov.radiostations.core.domain.mapper.country.DtoCountryMapperImpl
import com.alexeymerov.radiostations.core.dto.CountryDto
import com.alexeymerov.radiostations.core.test.MainDispatcherRule
import com.alexeymerov.radiostations.core.test.TestDiffCallback
import com.alexeymerov.radiostations.core.test.TestUpdateCallback
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountryUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var countryRepository: CountryRepository

    private lateinit var useCase: CountryUseCase

    private lateinit var pagingSource: CountryPagingSource

    @Before
    fun setup() {
        pagingSource = CountryPagingSource()

        coEvery { countryRepository.loadCountries() } answers {
            pagingSource.needLoadValidList = true
        }
        every { countryRepository.getCountries() } returns pagingSource

        every { countryRepository.getCountriesByText(any()) } answers {
            pagingSource.needLoadFilterList = true
            pagingSource
        }

        useCase = CountryUseCaseImpl(countryRepository, DtoCountryMapperImpl())
    }

    @Test
    fun `get countries initially returns empty data`() = runTest {
        val pagingData = useCase.getAllCountries().first()

        val differ = AsyncPagingDataDiffer(TestDiffCallback<CountryDto>(), TestUpdateCallback())
        val job = launch { differ.submitData(pagingData) }
        advanceUntilIdle()

        assertThat(differ.snapshot().items).isEmpty()

        job.cancel()
    }

    @Test
    fun `get countries if data exist returns valid list`() = runTest {
        useCase.loadCountries()

        val pagingData = useCase.getAllCountries().first()

        val differ = AsyncPagingDataDiffer(TestDiffCallback<CountryDto>(), TestUpdateCallback())
        val job = launch { differ.submitData(pagingData) }
        advanceUntilIdle()

        val items = differ.snapshot().items
        val testItem = items.find { it.englishName == "United Kingdom" }

        assertThat(items).isNotEmpty()
        assertThat(testItem).isNotNull()

        job.cancel()
    }

    @Test
    fun `get countries by text returns filtered list`() = runTest {
        val pagingData = useCase.getAllCountries("text").first()

        val differ = AsyncPagingDataDiffer(TestDiffCallback<CountryDto>(), TestUpdateCallback())
        val job = launch { differ.submitData(pagingData) }
        advanceUntilIdle()

        val items = differ.snapshot().items
        val testItem = items.find { it.englishName == "Test Text" }

        assertThat(items).hasSize(1)
        assertThat(testItem).isNotNull()

        job.cancel()
    }
}