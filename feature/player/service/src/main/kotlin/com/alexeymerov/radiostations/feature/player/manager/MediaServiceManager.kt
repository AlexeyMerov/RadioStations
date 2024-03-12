package com.alexeymerov.radiostations.feature.player.manager

import android.content.Intent
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto

interface MediaServiceManager {

    fun getStationRouteIfExist(intent: Intent): String?

    fun setupPlayer()

    fun processNewAudioItem(item: AudioItemDto)

    fun processPlayerState(state: PlayingUseCase.PlayerState)

    fun onStop()

}