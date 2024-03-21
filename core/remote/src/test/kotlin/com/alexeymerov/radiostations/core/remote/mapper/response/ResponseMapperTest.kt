package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.CountryIdd
import com.alexeymerov.radiostations.core.remote.response.CountryName
import com.alexeymerov.radiostations.core.remote.response.HeadBody
import com.alexeymerov.radiostations.core.remote.response.MainBody
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ResponseMapperTest {

    private lateinit var mapper: ResponseMapper

    @Before
    fun init() {
        mapper = ResponseMapperImpl()
    }

    @Test
    fun `map radio success response return list with same items`() {
        val testText = "TestText"
        val headBody = HeadBody(status = "200")
        val bodyList = listOf(CategoryBody(text = testText))
        val mainBody = MainBody(headBody, bodyList)
        val body = Response.success(mainBody)

        val mappedData = mapper.mapRadioResponseBody(body)
        val sameItemExist = mappedData.find { it.text == testText }

        assertThat(mappedData).isNotEmpty()
        assertThat(sameItemExist).isNotNull()
    }

    @Test
    fun `map radio response with any head status but 200 return empty list`() {
        val testText = "TestText"
        val headBody = HeadBody(status = "400")
        val bodyList = listOf(CategoryBody(text = testText))
        val mainBody = MainBody(headBody, bodyList)
        val body = Response.success(mainBody)

        val mappedData = mapper.mapRadioResponseBody(body)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map radio response with null body return empty list `() {
        val body = Response.success<MainBody<CategoryBody>?>(null)

        val mappedData = mapper.mapRadioResponseBody(body)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map radio response with error return empty list`() {
        val contentType = "application/json".toMediaTypeOrNull()
        val json = "{\"key\":[\"nothing\"]}"
        val responseBody = json.toResponseBody(contentType)
        val body = Response.error<MainBody<CategoryBody>>(400, responseBody)

        val mappedData = mapper.mapRadioResponseBody(body)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map country success response return list with same items`() {
        val countryName = mockk<CountryName>()
        val countryIdd = mockk<CountryIdd>()
        val testCca2 = "TestText"
        val bodyList = listOf(CountryBody(countryName, testCca2, countryIdd))
        val body = Response.success(bodyList)

        val mappedData = mapper.mapCountriesResponseBody(body)
        val sameItemExist = mappedData.find { it.cca2 == testCca2 }

        assertThat(mappedData).isNotEmpty()
        assertThat(sameItemExist).isNotNull()
    }

    @Test
    fun `map country response with null body return empty list`() {
        val body = Response.success<List<CountryBody>?>(null)

        val mappedData = mapper.mapCountriesResponseBody(body)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map country response with empty list body return empty list`() {
        val body = Response.success<List<CountryBody>>(emptyList())

        val mappedData = mapper.mapCountriesResponseBody(body)

        assertThat(mappedData).isEmpty()
    }

    @Test
    fun `map country response with error return empty list`() {
        val contentType = "application/json".toMediaTypeOrNull()
        val json = "{\"key\":[\"nothing\"]}"
        val responseBody = json.toResponseBody(contentType)
        val body = Response.error<List<CountryBody>>(400, responseBody)

        val mappedData = mapper.mapCountriesResponseBody(body)

        assertThat(mappedData).isEmpty()
    }
}