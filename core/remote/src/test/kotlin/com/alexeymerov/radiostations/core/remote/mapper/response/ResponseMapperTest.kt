package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.TestConst
import com.alexeymerov.radiostations.core.remote.client.bodyOrNull
import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.CountryIdd
import com.alexeymerov.radiostations.core.remote.response.CountryName
import com.alexeymerov.radiostations.core.remote.response.HeadBody
import com.alexeymerov.radiostations.core.remote.response.RadioMainBody
import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ResponseMapperTest {

    private lateinit var mapper: ResponseMapper

    @Before
    fun init() {
        mapper = ResponseMapperImpl()
    }

    private inline fun <reified T> getTestClient(body: RadioMainBody<T>?): HttpClient {
        val mainBodyJson = if (body != null) Json.encodeToString(body) else "null"
        return TestConst.getTestClient(mainBodyJson)
    }

    @Test
    fun `map radio success response return list with same items`() = runTest {
        val testText = "TestText"
        val headBody = HeadBody(status = "200")
        val bodyList = listOf(CategoryBody(text = testText))
        val mainBody = RadioMainBody(headBody, bodyList)

        val client = getTestClient(mainBody)
        val response = client.get("")
        val responseBody = response.bodyOrNull<RadioMainBody<CategoryBody>?>()

        val mappedData = mapper.mapRadioResponseBody(response, responseBody)
        val sameItemExist = mappedData.find { it.text == testText }

        assertThat(mappedData).isNotEmpty()
        assertThat(sameItemExist).isNotNull()
    }

    @Test
    fun `map radio response with any head status but 200 return empty list`() = runTest {
        val testText = "TestText"
        val headBody = HeadBody(status = "400")
        val bodyList = listOf(CategoryBody(text = testText))
        val mainBody = RadioMainBody(headBody, bodyList)

        val client = getTestClient(mainBody)
        val response = client.get("")
        val responseBody = response.bodyOrNull<RadioMainBody<CategoryBody>?>()

        val mappedData = mapper.mapRadioResponseBody(response, responseBody)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map radio response with null body return empty list `() = runTest {
        val client = getTestClient<RadioMainBody<CategoryBody>?>(null)
        val response = client.get("")
        val responseBody = response.bodyOrNull<RadioMainBody<CategoryBody>?>()

        val mappedData = mapper.mapRadioResponseBody(response, responseBody)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map radio response with error return empty list`() = runTest {
        val json = "{\"key\":[\"nothing\"]}"
        val client = TestConst.getTestClient(json)
        val response = client.get(TestConst.URL_ERROR)
        val responseBody = response.bodyOrNull<RadioMainBody<CategoryBody>?>()

        val mappedData = mapper.mapRadioResponseBody(response, responseBody)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map country success response return list with same items`() = runTest {
        val countryName = CountryName("", "", emptyMap())
        val countryIdd = CountryIdd("", emptyList())
        val testCca2 = "TestText"
        val bodyList = listOf(CountryBody(countryName, testCca2, countryIdd))

        val client = TestConst.getTestClient(Json.encodeToString(bodyList))
        val response = client.get("")
        val responseBody = response.bodyOrNull<List<CountryBody>?>()

        val mappedData = mapper.mapCountriesResponseBody(response, responseBody)
        val sameItemExist = mappedData.find { it.cca2 == testCca2 }

        assertThat(mappedData).isNotEmpty()
        assertThat(sameItemExist).isNotNull()
    }

    @Test
    fun `map country response with null body return empty list`() = runTest {
        val client = TestConst.getTestClient("")
        val response = client.get("")
        val responseBody = response.bodyOrNull<List<CountryBody>?>()

        val mappedData = mapper.mapCountriesResponseBody(response, responseBody)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map country response with empty list body return empty list`() = runTest {
        val client = TestConst.getTestClient(Json.encodeToString(emptyList<CountryBody>()))
        val response = client.get("")
        val responseBody = response.bodyOrNull<List<CountryBody>?>()

        val mappedData = mapper.mapCountriesResponseBody(response, responseBody)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map country response with error return empty list`() = runTest {
        val json = "{\"key\":[\"nothing\"]}"
        val client = TestConst.getTestClient(json)
        val response = client.get(TestConst.URL_ERROR)
        val responseBody = response.bodyOrNull<List<CountryBody>?>()

        val mappedData = mapper.mapCountriesResponseBody(response, responseBody)

        assertThat(mappedData).isEmpty()
    }
}