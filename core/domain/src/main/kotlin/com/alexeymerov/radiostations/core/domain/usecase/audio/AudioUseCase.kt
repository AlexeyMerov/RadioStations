package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto

interface AudioUseCase {

    suspend fun getMediaItem(url: String): AudioItemDto?

    suspend fun getByUrl(url: String): CategoryItemDto?

}