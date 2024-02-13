package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.data.repository.audio.FakeMediaRepository
import com.alexeymerov.radiostations.core.datastore.FakeSettingsStore
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapperImpl
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.*
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.google.common.truth.Truth.*
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import kotlinx.coroutines.flow.first
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
            DtoCategoriesMapperImpl(),
            FakeSettingsStore(),
            mockk<FirebaseAnalytics>(relaxed = true)
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

    @Test
    fun `getFavorites when not favorites return empty list`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()
    }

    @Test
    fun `setFavorite saves item and getFavorites returns it`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()

        val testItem = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)!!

        useCase.setFavorite(testItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isNotEmpty()

        val theSameItem = updatedItems.find { it.id == testItem.id }

        assertThat(theSameItem).isNotNull()
    }

    @Test
    fun `toggleFavorite(item) with true changes favorite state to false`() = runTest {
        val testItem = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)!!

        useCase.setFavorite(testItem)

        val items = useCase.getFavorites().first().items

        assertThat(items).isNotEmpty()

        val theSameItem = items.find { it.id == testItem.id }

        assertThat(theSameItem).isNotNull()
        theSameItem!!

        useCase.toggleFavorite(theSameItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isEmpty()
    }

    @Test
    fun `toggleFavorite(item) with false changes favorite state to true`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()

        val testItem = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)!!

        useCase.toggleFavorite(testItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isNotEmpty()
    }

    @Test
    fun `toggleFavorite(id) with true changes favorite state to false`() = runTest {
        val testItem = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)!!

        useCase.setFavorite(testItem)

        val items = useCase.getFavorites().first().items

        assertThat(items).isNotEmpty()

        val theSameItem = items.find { it.id == testItem.id }

        assertThat(theSameItem).isNotNull()
        theSameItem!!

        useCase.toggleFavorite(theSameItem.id)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isEmpty()
    }

    @Test
    fun `toggleFavorite(id) with false changes favorite state to true`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()

        val testItem = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)!!

        useCase.toggleFavorite(testItem.id)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isNotEmpty()
    }

    @Test
    fun `unfavorite removes item from favorites`() = runTest {
        val testItem = useCase.getByUrl(FakeMediaRepository.VALID_ITEM_URL)!!

        useCase.setFavorite(testItem)

        val items = useCase.getFavorites().first().items

        assertThat(items).isNotEmpty()

        useCase.unfavorite(testItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isEmpty()
    }

    @Test
    fun `getPlayerState return empty state by default`() = runTest {
        val state = useCase.getPlayerState().first()

        assertThat(state).isEqualTo(PlayerState.EMPTY)
    }

    @Test
    fun `updatePlayerState saves state and getPlayerState returns the same value`() = runTest {
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.EMPTY)

        useCase.updatePlayerState(PlayerState.PLAYING)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)

        useCase.updatePlayerState(PlayerState.STOPPED)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.STOPPED)

        useCase.updatePlayerState(PlayerState.LOADING)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.LOADING)
    }

    @Test
    fun `updatePlayerState with same EMPTY state returns the same value`() = runTest {
        useCase.updatePlayerState(PlayerState.EMPTY)
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.EMPTY)

        useCase.updatePlayerState(PlayerState.EMPTY)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.EMPTY)
    }

    @Test
    fun `updatePlayerState with same PLAYING state returns the same value`() = runTest {
        useCase.updatePlayerState(PlayerState.PLAYING)
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)

        useCase.updatePlayerState(PlayerState.PLAYING)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)
    }

    @Test
    fun `updatePlayerState with STOPPED state when current is EMPTY do nothing`() = runTest {
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.EMPTY)

        useCase.updatePlayerState(PlayerState.STOPPED)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.EMPTY)
    }

    @Test
    fun `updatePlayerState with same LOADING state returns the same value`() = runTest {
        useCase.updatePlayerState(PlayerState.LOADING)
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.LOADING)

        useCase.updatePlayerState(PlayerState.LOADING)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.LOADING)
    }

    @Test
    fun `togglePlayerPlayStop() if current state Stopped changes to Playing`() = runTest {
        useCase.updatePlayerState(PlayerState.PLAYING)

        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)

        useCase.updatePlayerState(PlayerState.STOPPED)
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.STOPPED)

        useCase.togglePlayerPlayStop()
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)
    }

    @Test
    fun `togglePlayerPlayStop() if current state Playing changes to Stopped`() = runTest {
        useCase.updatePlayerState(PlayerState.PLAYING)

        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)

        useCase.togglePlayerPlayStop()
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.STOPPED)
    }

    @Test
    fun `togglePlayerPlayStop() if current state EMPTY changes to Playing`() = runTest {
        useCase.updatePlayerState(PlayerState.EMPTY)
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.EMPTY)

        useCase.togglePlayerPlayStop()
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)
    }

    @Test
    fun `togglePlayerPlayStop() if current state LOADING changes to Playing`() = runTest {
        useCase.updatePlayerState(PlayerState.LOADING)
        var state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.LOADING)

        useCase.togglePlayerPlayStop()
        state = useCase.getPlayerState().first()
        assertThat(state).isEqualTo(PlayerState.PLAYING)
    }
}