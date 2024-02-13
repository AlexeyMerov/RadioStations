package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.api.CountryApi
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapperImpl
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.CountryIdd
import com.alexeymerov.radiostations.core.remote.response.CountryName
import com.google.common.truth.Truth.*
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class CountryClientTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var countryApi: CountryApi

    private lateinit var countryClient: CountryClient

    private val validCca2CountryList = listOf(
        CountryBody(
            cca2 = "CA",
            name = mockk<CountryName>(),
            idd = mockk<CountryIdd>()
        ),
        CountryBody(
            cca2 = "GB",
            name = mockk<CountryName>(),
            idd = mockk<CountryIdd>()
        )
    )

    @Before
    fun setup() {
        countryClient = CountryClientImpl(countryApi, ResponseMapperImpl())
    }

    @Test
    fun `client request countries returns valid data`() = runTest {
        coEvery { countryApi.getAllCountries(any()) } returns Response.success(validCca2CountryList)
        val countryList = countryClient.requestAllCountries()

        assertThat(countryList).isNotEmpty()
        assertThat(countryList[0].cca2).isEqualTo("CA")
    }

    @Test
    fun `client request countries with empty list returns empty list`() = runTest {
        coEvery { countryApi.getAllCountries(any()) } returns Response.success(emptyList())
        val countryList = countryClient.requestAllCountries()

        assertThat(countryList).isEmpty()
    }

    @Test
    fun `unsuccessful client request countries returns empty list`() = runTest {
        coEvery { countryApi.getAllCountries(any()) } returns Response.error(400, mockk(relaxed = true))
        val countryList = countryClient.requestAllCountries()

        assertThat(countryList).isEmpty()
    }

}

