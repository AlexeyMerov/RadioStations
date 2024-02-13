package com.alexeymerov.radiostations.core.data.repository.country

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.alexeymerov.radiostations.core.data.mapper.country.CountryMapperImpl
import com.alexeymerov.radiostations.core.database.dao.CountryDao
import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.client.country.CountryClient
import com.alexeymerov.radiostations.core.remote.client.country.FakeCountryClient
import com.google.common.truth.Truth.*
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountryRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    private lateinit var countryDao: CountryDao

    private lateinit var countryClient: CountryClient

    private lateinit var pagingSource: PagingSource<Int, CountryEntity>

    private lateinit var countries: List<CountryEntity>

    private lateinit var repository: CountryRepository

    @Before
    fun setup() = runTest {
        val mapper = CountryMapperImpl()

        val fakeCountryClient = FakeCountryClient()
        countryClient = fakeCountryClient
        countries = mapper.mapCountries(fakeCountryClient.countries)
        pagingSource = CountryPagingSource(countries)

        coJustRun { countryDao.insertAll(any()) }
        every { countryDao.getAll() } returns pagingSource
        every { countryDao.searchByText(any()) } returns pagingSource

        repository = CountryRepositoryImpl(
            countryClient = countryClient,
            countryDao = countryDao,
            countryMapper = mapper
        )
    }

    @Test
    fun `loadCountries saves list to DB`() = runTest {
        repository.loadCountries()

        coVerify { countryDao.insertAll(countries) }
    }

    @Test
    fun `getCountries return saved entity from db as paged data`() = runTest {
        val pagingSource = repository.getCountries()

        val pager = TestPager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 0,
                initialLoadSize = 10
            ),
            pagingSource = pagingSource
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).containsAnyIn(countries)
    }

    @Test
    fun `getCountriesByText return saved entity from db as paged data which contains text`() = runTest {
        val searchText = "Kingdom"
        val pagingSource = repository.getCountriesByText(searchText)

        val pager = TestPager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 0,
                initialLoadSize = 10
            ),
            pagingSource = pagingSource
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        val list = result.data

        list.filter {
            it.nameEnglish.contains(searchText, ignoreCase = true)
                || it.nameNative.contains(searchText, ignoreCase = true)
                || it.phoneCode.contains(searchText, ignoreCase = true)
        }
        assertThat(result.data).containsAnyIn(countries)
    }

}