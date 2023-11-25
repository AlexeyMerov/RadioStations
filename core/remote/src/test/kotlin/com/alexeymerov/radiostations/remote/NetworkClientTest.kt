package com.alexeymerov.radiostations.remote

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.remote.api.RadioApi
import com.alexeymerov.radiostations.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.remote.client.radio.RadioClientImpl
import com.alexeymerov.radiostations.remote.response.CategoryBody
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.InputStreamReader

class NetworkClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var radioClient: RadioClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val jsonFactory = NetworkDefaults.getJsonAdapterFactory()
        val moshi = NetworkDefaults.getMoshi(jsonFactory)
        val moshiConverterFactory = NetworkDefaults.getConverterFactory(moshi)
        val jsonInterceptor = NetworkDefaults.getForceJsonInterceptor()
        val okHttpClient = NetworkDefaults.getOkHttpClient(forTest = true, jsonInterceptor)
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(mockWebServer.url(String.EMPTY))
            .addConverterFactory(moshiConverterFactory)
            .build()

        val radioApi = retrofit.create(RadioApi::class.java)

        radioClient = RadioClientImpl(radioApi)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `all network queries are with render=json param`() = runTest {
        val responseBody = readResourceFile(RESPONSE_200_CATEGORIES)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioClient.requestCategoriesByUrl(String.EMPTY)
        val renderParam = response.raw().request.url.queryParameter(NetworkDefaults.QUERY_RENDER_NAME)
        assert(renderParam == NetworkDefaults.QUERY_RENDER_JSON_PARAMETER)
    }

    @Test
    fun `mocked general Categories from network`() = runTest {
        val responseBody = readResourceFile(RESPONSE_200_CATEGORIES)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioClient.requestCategoriesByUrl(String.EMPTY)
        val categoryList = response.body()?.body
        if (categoryList == null) assert(false)
        else {
            assert(categoryList.isNotEmpty())
            assert(categoryList[0].text == CATEGORY_NAME_WORLD_MUSIC)
        }
    }

    @Test
    fun `mocked Top40 categories with headers and audio from network`() = runTest {
        val responseBody = readResourceFile(RESPONSE_200_TOP40)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioClient.requestCategoriesByUrl(String.EMPTY)
        val audiosList = response.body()?.body
        if (audiosList == null) assert(false)
        else {
            assert(audiosList.any { it.children != null })

            val headersWithAudioList = mutableListOf<CategoryBody>()
            audiosList.forEach { category ->
                category.children?.let {
                    headersWithAudioList.addAll(it)
                }
            }
            assert(headersWithAudioList.any { it.type == TYPE_LINK })
            assert(headersWithAudioList.any { it.type == TYPE_AUDIO })
        }
    }

    @Test
    fun `mocked audio response from network`() = runTest {
        val responseBody = readResourceFile(RESPONSE_200_AUDIO)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioClient.requestAudioByUrl(String.EMPTY)
        val responseList = response.body()?.body
        if (responseList == null) assert(false)
        else {
            val audio = responseList[0]
            assert(audio.url.matches(NetworkDefaults.REGEX_VALID_URL))
            assert(audio.bitrate > 0)
            assert(audio.mediaType == "mp3")
        }
    }

    private fun readResourceFile(fileName: String): String {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader?.getResourceAsStream(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.use { it.readText() }
    }

    private companion object {
        const val RESPONSE_200_CATEGORIES = "response_200_categories.json"
        const val RESPONSE_200_TOP40 = "response_200_top40.json"
        const val RESPONSE_200_AUDIO = "response_200_audio.json"
        const val CATEGORY_NAME_WORLD_MUSIC = "World Music"
        const val TYPE_AUDIO = "audio"
        const val TYPE_LINK = "link"
    }
}

