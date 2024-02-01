package com.alexeymerov.radiostations.core.data.repository

import com.alexeymerov.radiostations.core.data.mapper.media.MediaMapper
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepositoryImpl
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.database.dao.MediaDao
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.core.remote.response.MediaBody
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MediaRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var client: RadioClient

    @MockK
    lateinit var categoryDao: CategoryDao

    @MockK
    lateinit var mediaDao: MediaDao

    @MockK
    lateinit var mediaMapper: MediaMapper

    private lateinit var repository: MediaRepository

    @Before
    fun setup() {
        repository = spyk(MediaRepositoryImpl(client, categoryDao, mediaMapper, mediaDao))
    }

    @Test
    fun `load audio by broken url return null`() = runTest {
        coEvery { categoryDao.getByUrl(any()) } returns mockk<CategoryEntity>()
        coEvery { client.requestAudioByUrl(any()) } returns null

        val audioByUrl = repository.getMediaByUrl("")

        assertThat(audioByUrl).isNull()

        coVerifyOrder {
            repository.getMediaByUrl(any())
            client.requestAudioByUrl(any())
        }

        confirmVerified(repository, client)
    }

    @Test
    fun `load audio by valid url return MediaEntity`() = runTest {
        coEvery { categoryDao.getByUrl(any()) } returns mockk<CategoryEntity>()
        coEvery { client.requestAudioByUrl(any()) } returns mockk<MediaBody>()
        every { mediaMapper.mapToEntity(any(), any()) } returns mockk<MediaEntity>()

        val mediaEntity = repository.getMediaByUrl("")

        assertThat(mediaEntity).isNotNull()
        assertThat(mediaEntity).isInstanceOf(MediaEntity::class.java)

        coVerifyOrder {
            repository.getMediaByUrl(any())
            client.requestAudioByUrl(any())
            mediaMapper.mapToEntity(any(), any())
        }

        confirmVerified(repository, client, mediaMapper)
    }


}