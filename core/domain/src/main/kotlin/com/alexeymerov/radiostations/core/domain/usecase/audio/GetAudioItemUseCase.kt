package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.dto.AudioItemDto

interface GetAudioItemUseCase {

    suspend operator fun invoke(tuneId: String): AudioItemDto?

}