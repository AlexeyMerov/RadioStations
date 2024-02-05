package com.alexeymerov.radiostations.core.remote.api

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.remote.TestConst
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
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
class RadioApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var radioApi: RadioApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = NetworkDefaults.getTestRetrofit(mockWebServer.url(String.EMPTY))

        radioApi = retrofit.create(RadioApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `api request categories returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.CATEGORIES_RESPONSE_200)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))


        val response = radioApi.getCategoriesByUrl(String.EMPTY)
        val categoryList = response.body()?.body

        assertThat(categoryList).isNotNull()
        assertThat(categoryList).isNotEmpty()
        assertThat(categoryList!![0].text).isEqualTo(TestConst.CATEGORY_NAME_WORLD_MUSIC)
    }

    @Test
    fun `api request categories with invalid response returns error`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("[{},{}]"))

        try {
            radioApi.getCategoriesByUrl(String.EMPTY)
        } catch (e: JsonDataException) {
            assertThat(e).isInstanceOf(JsonDataException::class.java)
        }
    }

    @Test
    fun `unsuccessful api request categories returns unsuccessful status`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val response = radioApi.getCategoriesByUrl(String.EMPTY)

        assertThat(response.isSuccessful).isFalse()
    }

    @Test
    fun `api request categories with subcategories and audio returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.CATEGORIES_TOP40_RESPONSE_200)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioApi.getCategoriesByUrl(String.EMPTY)
        val audiosList = response.body()?.body

        assertThat(audiosList).isNotNull()
        audiosList!!

        val categoriesWithChildren = audiosList.flatMap { it.children ?: emptyList() }
        assertThat(categoriesWithChildren).isNotEmpty()

        val hasSubcategories = categoriesWithChildren.any { it.type == TestConst.TYPE_LINK }
        assertThat(hasSubcategories).isTrue()

        val hasStations = categoriesWithChildren.any { it.type == TestConst.TYPE_AUDIO }
        assertThat(hasStations).isTrue()
    }

    @Test
    fun `api request audio returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.AUDIO_RESPONSE_200)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioApi.getAudioByUrl(String.EMPTY)
        val mediaList = response.body()?.body

        assertThat(mediaList).isNotNull()
        mediaList!!

        assertThat(mediaList).isNotEmpty()

        val audio = mediaList[0]
        assertThat(audio.url).matches(NetworkDefaults.REGEX_VALID_URL.pattern)
        assertThat(audio.mediaType).isEqualTo(TestConst.VALID_MEDIA_TYPE)
    }

    @Test
    fun `api request audio with invalid response returns error`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("[{},{}]"))

        try {
            radioApi.getAudioByUrl(String.EMPTY)
        } catch (e: JsonDataException) {
            assertThat(e).isInstanceOf(JsonDataException::class.java)
        }
    }

    @Test
    fun `unsuccessful api request audio returns unsuccessful status`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val response = radioApi.getAudioByUrl(String.EMPTY)

        assertThat(response.isSuccessful).isFalse()
    }

}

