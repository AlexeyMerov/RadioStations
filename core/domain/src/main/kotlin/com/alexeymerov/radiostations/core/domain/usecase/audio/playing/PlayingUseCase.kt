package com.alexeymerov.radiostations.core.domain.usecase.audio.playing

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import kotlinx.coroutines.flow.Flow

interface PlayingUseCase {

    fun getLastPlayingMediaItem(): Flow<AudioItemDto?>

    suspend fun setLastPlayingMedia(item: AudioItemDto)

    fun getPlayerState(): Flow<PlayerState>

    suspend fun updatePlayerState(newState: PlayerState)

    suspend fun togglePlayerPlayStop()

    sealed class PlayerState(val value: Int, open val isUserAction: Boolean = false) {
        data object Empty : PlayerState(0)
        data class Playing(override val isUserAction: Boolean = false) : PlayerState(1, isUserAction)
        data class Stopped(override val isUserAction: Boolean = false) : PlayerState(2, isUserAction)
        data object Loading : PlayerState(3)
    }

}