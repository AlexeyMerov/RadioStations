package com.alexeymerov.radiostations.core.data.repository.audio

import com.alexeymerov.radiostations.core.data.mapper.media.MediaMapper
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
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
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
        coEvery { categoryDao.getByTuneId(any()) } returns mockk<CategoryEntity>()
        coEvery { client.requestAudioById(any()) } returns null

        val audioByUrl = repository.getMediaByTuneId("")

        assertThat(audioByUrl).isNull()

        coVerifyOrder {
            repository.getMediaByTuneId(any())
            client.requestAudioById(any())
        }

        confirmVerified(repository, client)
    }

    @Test
    fun `load audio by valid url return MediaEntity`() = runTest {
        coEvery { categoryDao.getByTuneId(any()) } returns mockk<CategoryEntity>()
        coEvery { client.requestAudioById(any()) } returns mockk<MediaBody>()
        every { mediaMapper.mapToEntity(any(), any()) } returns mockk<MediaEntity>()

        val mediaEntity = repository.getMediaByTuneId("")

        assertThat(mediaEntity).isNotNull()
        assertThat(mediaEntity).isInstanceOf(MediaEntity::class.java)

        coVerifyOrder {
            repository.getMediaByTuneId(any())
            client.requestAudioById(any())
            mediaMapper.mapToEntity(any(), any())
        }

        confirmVerified(repository, client, mediaMapper)
    }


}