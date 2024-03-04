package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.data.repository.audio.FakeMediaRepository
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapperImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AudioUseCaseTest {

    private lateinit var useCase: AudioUseCase

    @Before
    fun setup() {
        useCase = AudioUseCaseImpl(
            FakeMediaRepository(),
            DtoCategoriesMapperImpl()
        )
    }

    @Test
    fun `get media with invalid url returns null`() = runTest {
        val item = useCase.getMediaItem("")

        assertThat(item).isNull()
    }

    @Test
    fun `get media with valid url returns valid item`() = runTest {
        val item = useCase.getMediaItem(FakeMediaRepository.VALID_MEDIA_URL)

        assertThat(item).isNotNull()
        item!!

        assertThat(item.parentUrl).isEqualTo(FakeMediaRepository.VALID_MEDIA_URL)
    }

    @Test
    fun `getByUrl with valid url returns valid item`() = runTest {
        val item = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)

        assertThat(item).isNotNull()
        item!!

        assertThat(item.url).isEqualTo(FakeMediaRepository.VALID_ITEM_URL)
    }

    @Test
    fun `getByUrl with invalid url returns null`() = runTest {
        val item = useCase.getByUrl("")

        assertThat(item).isNull()
    }
}