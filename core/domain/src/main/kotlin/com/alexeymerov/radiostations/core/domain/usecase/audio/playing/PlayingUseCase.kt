package com.alexeymerov.radiostations.core.domain.usecase.audio.playing

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import kotlinx.coroutines.flow.Flow

interface PlayingUseCase {

    fun getLastPlayingMediaItem(): Flow<AudioItemDto?>

    suspend fun setLastPlayingMedia(item: AudioItemDto)

    fun getPlayerState(): Flow<PlayerState>

    suspend fun updatePlayerState(newState: PlayerState)

    suspend fun togglePlayerPlayStop()

    enum class PlayerState(val value: Int) {
        EMPTY(0),
        PLAYING(1),
        STOPPED(2),
        LOADING(3)
    }

}