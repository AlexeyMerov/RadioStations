package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.data.mapper.media.MediaMapper
import com.alexeymerov.radiostations.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.data.repository.audio.MediaRepositoryImpl
import com.alexeymerov.radiostations.database.dao.CategoryDao
import com.alexeymerov.radiostations.database.dao.MediaDao
import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.database.entity.MediaEntity
import com.alexeymerov.radiostations.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.remote.response.MediaBody
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
    fun `load audio by url failed`() = runTest {
        val categoryEntity = mockk<CategoryEntity>()
        coEvery { categoryDao.getByUrl(any()) } returns categoryEntity
        coEvery { client.requestAudioByUrl(any()) } returns null

        val audioByUrl = repository.getMediaByUrl("")
        assert(audioByUrl == null)

        coVerifyOrder {
            repository.getMediaByUrl(any())
            client.requestAudioByUrl(any())
        }

        confirmVerified(repository, client)
    }

    @Test
    fun `load audio by url success`() = runTest {
        val categoryEntity = mockk<CategoryEntity>()
        val mediaBody = mockk<MediaBody>()
        val mediaEntity = mockk<MediaEntity>()
        coEvery { categoryDao.getByUrl(any()) } returns categoryEntity
        coEvery { client.requestAudioByUrl(any()) } returns mediaBody
        every { mediaMapper.mapToEntity(any(), any()) } returns mediaEntity

        val audioByUrl = repository.getMediaByUrl("")
        assert(audioByUrl != null)

        coVerifyOrder {
            repository.getMediaByUrl(any())
            client.requestAudioByUrl(any())
            mediaMapper.mapToEntity(any(), any())
        }

        confirmVerified(repository, client, mediaMapper)
    }


}