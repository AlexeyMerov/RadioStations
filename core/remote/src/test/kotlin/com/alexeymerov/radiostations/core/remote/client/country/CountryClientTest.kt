package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.TestConst
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapperImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountryClientTest {

    @Test
    fun `client request countries returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.COUNTRIES_CA_UK_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody)
        val countryClient = CountryClientImpl(httpClient, ResponseMapperImpl())

        val countryList = countryClient.requestAllCountries()

        assertThat(countryList).isNotEmpty()
        assertThat(countryList[0].cca2).isEqualTo("CA")
        assertThat(countryList[1].cca2).isEqualTo("GB")
    }

    @Test
    fun `client request countries with empty list returns empty list`() = runTest {
        val httpClient = TestConst.getTestClient(TestConst.EMPTY_RESPONSE)
        val countryClient = CountryClientImpl(httpClient, ResponseMapperImpl())

        val countryList = countryClient.requestAllCountries()

        assertThat(countryList).isEmpty()
    }

    @Test
    fun `unsuccessful client request countries returns empty list`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.COUNTRIES_CA_UK_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody, returnError = true)
        val countryClient = CountryClientImpl(httpClient, ResponseMapperImpl())

        val countryList = countryClient.requestAllCountries()

        assertThat(countryList).isEmpty()
    }

}

