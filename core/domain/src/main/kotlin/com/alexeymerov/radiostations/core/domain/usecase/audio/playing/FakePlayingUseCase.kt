package com.alexeymerov.radiostations.core.domain.usecase.audio.playing

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePlayingUseCase : PlayingUseCase {

    private val playingState = MutableStateFlow(PlayingUseCase.PlayerState.EMPTY)

    private val playingAudio = MutableStateFlow<AudioItemDto?>(null)

    override fun getPlayerState(): Flow<PlayingUseCase.PlayerState> {
        return playingState
    }

    override suspend fun updatePlayerState(newState: PlayingUseCase.PlayerState) {
        playingState.value = newState
    }

    override suspend fun togglePlayerPlayStop() {
        if (playingState.value == PlayingUseCase.PlayerState.PLAYING) {
            playingState.value = PlayingUseCase.PlayerState.STOPPED
        } else if (playingState.value == PlayingUseCase.PlayerState.STOPPED) {
            playingState.value = PlayingUseCase.PlayerState.LOADING

            delay(500)

            playingState.value = PlayingUseCase.PlayerState.PLAYING
        }
    }

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        return playingAudio
    }

    override suspend fun setLastPlayingMedia(item: AudioItemDto) {
        playingAudio.value = item
    }
}