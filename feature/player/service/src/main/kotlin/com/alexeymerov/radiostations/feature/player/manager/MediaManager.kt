package com.alexeymerov.radiostations.feature.player.manager

import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto

interface MediaManager {

    fun setupPlayer()

    fun processNewAudioItem(item: AudioItemDto)

    fun processPlayerState(state: PlayingUseCase.PlayerState)

    fun onStop()

    companion object {
        const val DYNAMIC_SHORTCUT_ID = "latest_station_static_id"
    }

}