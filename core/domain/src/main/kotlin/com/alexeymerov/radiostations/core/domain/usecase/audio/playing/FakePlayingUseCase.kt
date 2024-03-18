package com.alexeymerov.radiostations.core.domain.usecase.audio.playing

import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePlayingUseCase : PlayingUseCase {

    private val playingState = MutableStateFlow<PlayerState>(PlayerState.Empty)

    private val playingAudio = MutableStateFlow<AudioItemDto?>(null)

    override fun getPlayerState(): Flow<PlayerState> {
        return playingState
    }

    override suspend fun updatePlayerState(newState: PlayerState) {
        playingState.value = newState
    }

    override suspend fun togglePlayerPlayStop() {
        if (playingState.value is PlayerState.Playing) {
            playingState.value = PlayerState.Stopped(true)
        } else if (playingState.value is PlayerState.Stopped) {
            playingState.value = PlayerState.Loading

            delay(500)

            playingState.value = PlayerState.Playing(true)
        }
    }

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        return playingAudio
    }

    override suspend fun setLastPlayingMedia(item: AudioItemDto) {
        playingAudio.value = item
    }
}