package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FakeAudioUseCase : AudioUseCase {

    var flowDelay = 0L

    var emulateError = false

    var returnEmptyList = false

    private val currentFavorites = mutableListOf(
        CategoryItemDto(
            id = "id",
            url = "url",
            subTitle = "Hello",
            text = "Station Name",
            type = DtoItemType.AUDIO,
            isFavorite = true,
            initials = "HA"
        ),
        CategoryItemDto(
            id = "id1",
            url = "url1",
            subTitle = "Hello1",
            text = "Station Name Station Name",
            type = DtoItemType.AUDIO,
            isFavorite = true,
            initials = "HB"
        )
    )

    private val currentFavoritesFlow = MutableStateFlow(currentFavorites)

    override fun getFavorites(): Flow<CategoryDto> {
        return currentFavoritesFlow
            .map {
                val list = if (returnEmptyList) emptyList() else it
                CategoryDto(list, emulateError)
            }
            .onStart { delay(flowDelay) }
    }

    override suspend fun setFavorite(item: CategoryItemDto) {
        currentFavorites.add(item)
        currentFavoritesFlow.value = currentFavorites
    }

    override suspend fun unfavorite(item: CategoryItemDto) {
        currentFavorites.remove(item)
        currentFavoritesFlow.value = currentFavorites
    }

    override suspend fun toggleFavorite(item: CategoryItemDto) {
        if (currentFavorites.contains(item)) {
            unfavorite(item)
        } else {
            setFavorite(item)
        }
    }

    override suspend fun toggleFavorite(id: String) {
        val item = currentFavorites.firstOrNull { it.id == id }
        if (item != null) toggleFavorite(item)
    }

    override suspend fun getMediaItem(url: String): AudioItemDto? {
        // not required at the moment
        return null
    }

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        // not required at the moment
        return flowOf(null)
    }

    override suspend fun setLastPlayingMedia(item: AudioItemDto) {
        // not required at the moment
    }

    override suspend fun getByUrl(url: String): CategoryItemDto? {
        // not required at the moment
        return null
    }

    override fun getPlayerState(): Flow<AudioUseCase.PlayerState> {
        // not required at the moment
        return flowOf(AudioUseCase.PlayerState.EMPTY)
    }

    override suspend fun updatePlayerState(newState: AudioUseCase.PlayerState) {
        // not required at the moment
    }

    override suspend fun togglePlayerPlayStop() {
        // not required at the moment
    }

}