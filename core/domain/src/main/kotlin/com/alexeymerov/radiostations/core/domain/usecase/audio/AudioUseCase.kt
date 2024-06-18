package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.dto.AudioItemDto

interface AudioUseCase {

    suspend fun getMediaItem(tuneId: String): AudioItemDto?

}