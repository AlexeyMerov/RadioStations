package com.alexeymerov.radiostations.core.remote.api

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.remote.TestConst
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.core.remote.client.country.CountryClientImpl
import com.google.common.truth.Truth.*
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountryApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var countryApi: CountryApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = NetworkDefaults.getTestRetrofit(mockWebServer.url(String.EMPTY))

        countryApi = retrofit.create(CountryApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `api request countries returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.COUNTRIES_CA_UK_RESPONSE_200)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = countryApi.getAllCountries(CountryClientImpl.ALL_FIELDS)
        val countryList = response.body()

        assertThat(countryList).isNotNull()
        countryList!!

        assertThat(countryList).isNotEmpty()
        assertThat(countryList[0].cca2).isEqualTo("CA")
        assertThat(countryList[1].cca2).isEqualTo("GB")
    }

    @Test
    fun `api request countries with invalid response returns error`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("[{},{}]"))

        try {
            countryApi.getAllCountries(CountryClientImpl.ALL_FIELDS)
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(JsonDataException::class.java)
        }
    }

    @Test
    fun `unsuccessful api request countries returns unsuccessful status`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val response = countryApi.getAllCountries(CountryClientImpl.ALL_FIELDS)

        assertThat(response.isSuccessful).isFalse()
    }

}

