package com.alexeymerov.radiostations.core.remote.client.radio

import com.alexeymerov.radiostations.core.remote.api.RadioApi
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapperImpl
import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.HeadBody
import com.alexeymerov.radiostations.core.remote.response.MainBody
import com.alexeymerov.radiostations.core.remote.response.MediaBody
import com.google.common.truth.Truth.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class RadioClientTest {

    private val radioApi = mockk<RadioApi>()
    private lateinit var radioClient: RadioClient

    @Before
    fun setup() {
        radioClient = RadioClientImpl(radioApi, ResponseMapperImpl())
    }

    @Test
    fun `client request categories returns valid data`() = runTest {
        val testText = "TestText1"
        val validTextCategoryList = listOf(
            CategoryBody(text = testText),
            CategoryBody(text = "TestText2"),
        )
        coEvery { radioApi.getCategoriesByUrl(any()) } returns Response.success(MainBody(HeadBody("200"), validTextCategoryList))
        val categoriesList = radioClient.requestCategoriesByUrl("")

        assertThat(categoriesList).isNotEmpty()
        assertThat(categoriesList[0].text).isEqualTo(testText)
    }

    @Test
    fun `client request categories with empty list returns empty list`() = runTest {
        coEvery { radioApi.getCategoriesByUrl(any()) } returns Response.success(MainBody(mockk(relaxed = true), emptyList()))
        val categoriesList = radioClient.requestCategoriesByUrl("")

        assertThat(categoriesList).isEmpty()
    }

    @Test
    fun `unsuccessful client request categories returns empty list`() = runTest {
        coEvery { radioApi.getCategoriesByUrl(any()) } returns Response.error(400, mockk(relaxed = true))
        val categoriesList = radioClient.requestCategoriesByUrl("")

        assertThat(categoriesList).isEmpty()
    }

    @Test
    fun `client request audio returns valid data`() = runTest {
        val testText = "TestText1"
        val validTextCategoryList = listOf(
            MediaBody(url = testText),
        )
        coEvery { radioApi.getAudioByUrl(any()) } returns Response.success(MainBody(HeadBody("200"), validTextCategoryList))
        val mediaBody = radioClient.requestAudioByUrl("")

        assertThat(mediaBody).isNotNull()
        mediaBody!!

        assertThat(mediaBody.url).isEqualTo(testText)
    }

    @Test
    fun `client request audio with empty list returns null`() = runTest {
        coEvery { radioApi.getAudioByUrl(any()) } returns Response.success(MainBody(mockk(relaxed = true), emptyList()))
        val mediaBody = radioClient.requestAudioByUrl("")

        assertThat(mediaBody).isNull()
    }

    @Test
    fun `unsuccessful client request audio returns null`() = runTest {
        coEvery { radioApi.getAudioByUrl(any()) } returns Response.error(400, mockk(relaxed = true))
        val mediaBody = radioClient.requestAudioByUrl("")

        assertThat(mediaBody).isNull()
    }

}

