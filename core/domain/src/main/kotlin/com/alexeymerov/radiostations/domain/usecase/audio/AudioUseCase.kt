package com.alexeymerov.radiostations.domain.usecase.audio

import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import kotlinx.coroutines.flow.Flow

interface AudioUseCase {

    suspend fun getMediaItem(url: String): AudioItemDto?

    fun getLastPlayingMediaItem(): Flow<AudioItemDto?>

    suspend fun updateMediaAndPlay(item: AudioItemDto)

    suspend fun setLastPlayingMediaItem(item: AudioItemDto)

    suspend fun getByUrl(url: String): CategoryItemDto

    fun getFavorites(): Flow<CategoryDto>

    suspend fun toggleFavorite(item: CategoryItemDto)

    suspend fun toggleFavorite(id: String)

    suspend fun unfavorite(item: CategoryItemDto)

    fun getPlayerState(): Flow<PlayerState>

    suspend fun updatePlayerState(state: PlayerState)

    suspend fun togglePlayerPlayStop()

    enum class PlayerState(val value: Int) {
        EMPTY(0),
        PLAYING(1),
        STOPPED(2),
        BUFFERING(3)
    }

}