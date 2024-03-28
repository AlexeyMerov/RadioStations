package com.alexeymerov.radiostations.core.remote.client.radio

import com.alexeymerov.radiostations.core.remote.TestConst
import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapperImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RadioClientTest {

    @Test
    fun `client request categories returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.CATEGORIES_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val categoriesList = radioClient.requestCategoriesByUrl("")

        assertThat(categoriesList).isNotEmpty()
        assertThat(categoriesList[0].text).isEqualTo(TestConst.CATEGORY_NAME_WORLD_MUSIC)
    }

    @Test
    fun `client request categories with empty list returns empty list`() = runTest {
        val httpClient = TestConst.getTestClient(TestConst.EMPTY_RESPONSE)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val categoriesList = radioClient.requestCategoriesByUrl("")

        assertThat(categoriesList).isEmpty()
    }

    @Test
    fun `unsuccessful client request categories returns empty list`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.CATEGORIES_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody, returnError = true)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val categoriesList = radioClient.requestCategoriesByUrl("")

        assertThat(categoriesList).isEmpty()
    }

    @Test
    fun `request categories with subcategories and audio returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.CATEGORIES_TOP40_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val audiosList = radioClient.requestCategoriesByUrl("")

        assertThat(audiosList).isNotNull()

        val categoriesWithChildren = audiosList.flatMap { it.children ?: emptyList() }
        assertThat(categoriesWithChildren).isNotEmpty()

        val hasSubcategories = categoriesWithChildren.any { it.type == TestConst.TYPE_LINK }
        assertThat(hasSubcategories).isTrue()

        val hasStations = categoriesWithChildren.any { it.type == TestConst.TYPE_AUDIO }
        assertThat(hasStations).isTrue()
    }

    @Test
    fun `client request audio returns valid data`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.AUDIO_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val mediaBody = radioClient.requestAudioById("")

        assertThat(mediaBody).isNotNull()
        mediaBody!!

        assertThat(mediaBody.url).matches(NetworkDefaults.REGEX_VALID_URL.pattern)
        assertThat(mediaBody.mediaType).isEqualTo(TestConst.VALID_MEDIA_TYPE)
    }

    @Test
    fun `client request audio with empty list returns null`() = runTest {
        val httpClient = TestConst.getTestClient(TestConst.EMPTY_RESPONSE)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val mediaBody = radioClient.requestAudioById("")

        assertThat(mediaBody).isNull()
    }

    @Test
    fun `unsuccessful client request audio returns null`() = runTest {
        val responseBody = TestConst.readResourceFile(TestConst.AUDIO_RESPONSE_200)
        val httpClient = TestConst.getTestClient(responseBody, returnError = true)
        val radioClient = RadioClientImpl(httpClient, ResponseMapperImpl())
        val mediaBody = radioClient.requestAudioById("")

        assertThat(mediaBody).isNull()
    }

}

