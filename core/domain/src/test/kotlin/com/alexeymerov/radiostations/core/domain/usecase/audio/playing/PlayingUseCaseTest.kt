package com.alexeymerov.radiostations.core.domain.usecase.audio.playing

import com.alexeymerov.radiostations.core.data.repository.audio.FakeMediaRepository
import com.alexeymerov.radiostations.core.datastore.FakeSettingsStore
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.google.common.truth.Truth.assertThat
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PlayingUseCaseTest {

    private lateinit var useCase: PlayingUseCase

    @Before
    fun setup() {
        useCase = PlayingUseCaseImpl(
            FakeMediaRepository(),
            FakeSettingsStore(),
            mockk<FirebaseAnalytics>(relaxed = true)
        )
    }

    @Test
    fun `get last playing media if not exist returns null`() = runTest {
        val item = useCase.getLastPlayingMediaItem().first()

        assertThat(item).isNull()
    }

    @Test
    fun `get last playing media if exist returns valid item`() = runTest {
        val item = useCase.getLastPlayingMediaItem().first()

        assertThat(item).isNull()

        val itemDto = AudioItemDto(
            parentUrl = "parentUrl",
            directUrl = "directUrl",
            image = "image",
            title = "title",
            subTitle = "subTitle"
        )
        useCase.setLastPlayingMedia(itemDto)

        val updatedItem = useCase.getLastPlayingMediaItem().first()

        assertThat(updatedItem).isNotNull()
        updatedItem!!

        assertThat(updatedItem.parentUrl).isEqualTo(itemDto.parentUrl)
        assertThat(updatedItem.directUrl).isEqualTo(itemDto.directUrl)
        assertThat(updatedItem.image).isEqualTo(itemDto.image)
        assertThat(updatedItem.title).isEqualTo(itemDto.title)
        assertThat(updatedItem.subTitle).isEqualTo(itemDto.subTitle)
    }

    @Test
    fun `set last playing media changes previous item`() = runTest {
        val item = useCase.getLastPlayingMediaItem().first()

        assertThat(item).isNull()

        val itemDto = AudioItemDto(
            parentUrl = "parentUrl",
            directUrl = "directUrl",
            image = "image",
            title = "title",
            subTitle = "subTitle"
        )
        useCase.setLastPlayingMedia(itemDto)

        var updatedItem = useCase.getLastPlayingMediaItem().first()

        assertThat(updatedItem).isNotNull()
        updatedItem!!

        assertThat(updatedItem.parentUrl).isEqualTo(itemDto.parentUrl)
        assertThat(updatedItem.directUrl).isEqualTo(itemDto.directUrl)
        assertThat(updatedItem.image).isEqualTo(itemDto.image)
        assertThat(updatedItem.title).isEqualTo(itemDto.title)
        assertThat(updatedItem.subTitle).isEqualTo(itemDto.subTitle)

        val newItemDto = AudioItemDto(
            parentUrl = "parentUrl",
            directUrl = "directUrl",
            image = "image",
            title = "title",
            subTitle = "subTitle"
        )

        useCase.setLastPlayingMedia(newItemDto)

        updatedItem = useCase.getLastPlayingMediaItem().first()

        assertThat(updatedItem).isNotNull()
        updatedItem!!

        assertThat(updatedItem.parentUrl).isEqualTo(newItemDto.parentUrl)
        assertThat(updatedItem.directUrl).isEqualTo(newItemDto.directUrl)
        assertThat(updatedItem.image).isEqualTo(newItemDto.image)
        assertThat(updatedItem.title).isEqualTo(newItemDto.title)
        assertThat(updatedItem.subTitle).isEqualTo(newItemDto.subTitle)
    }
}