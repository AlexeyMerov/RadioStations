package com.alexeymerov.radiostations.mediaservice

import android.content.Context
import android.content.Intent
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase
import com.alexeymerov.radiostations.core.dto.AudioItemDto

interface MediaServiceManager {

    fun getStationRouteIfExist(intent: Intent): String?

    fun setupPlayer(context: Context)

    fun processCurrentAudioItem(context: Context, item: AudioItemDto)

    fun processPlayerState(context: Context, state: PlayingUseCase.PlayerState, currentMedia: AudioItemDto?)

    fun onStop()

}