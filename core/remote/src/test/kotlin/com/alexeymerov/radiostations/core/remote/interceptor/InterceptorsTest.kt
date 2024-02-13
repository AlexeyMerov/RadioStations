package com.alexeymerov.radiostations.core.remote.interceptor

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.remote.TestConst
import com.alexeymerov.radiostations.core.remote.api.RadioApi
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClientImpl
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.google.common.truth.Truth.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class InterceptorsTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var mockWebServer: MockWebServer
    private lateinit var radioClient: RadioClient
    private lateinit var radioApi: RadioApi

    @MockK
    private lateinit var responseMapper: ResponseMapper

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = NetworkDefaults.getTestRetrofit(mockWebServer.url(String.EMPTY))

        radioApi = retrofit.create(RadioApi::class.java)
        radioClient = RadioClientImpl(radioApi, responseMapper)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `interceptor adds 'render=json' query to all radio client requests`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.CATEGORIES_RESPONSE_200)
        mockWebServer.enqueue(MockResponse().setBody(responseBody))

        val response = radioApi.getCategoriesByUrl(String.EMPTY)
        val renderParam = response.raw().request.url.queryParameter(NetworkDefaults.QUERY_RENDER_NAME)

        assertThat(renderParam).isEqualTo(NetworkDefaults.QUERY_RENDER_JSON_PARAMETER)
    }
}

