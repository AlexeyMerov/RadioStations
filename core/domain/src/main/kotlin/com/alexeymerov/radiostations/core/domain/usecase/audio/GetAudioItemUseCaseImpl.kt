package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.common.extractTextFromRoundBrackets
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import javax.inject.Inject

class GetAudioItemUseCaseImpl @Inject constructor(
    private val mediaRepository: MediaRepository
) : GetAudioItemUseCase {

    override suspend fun invoke(tuneId: String): AudioItemDto? {
        val mediaEntity = mediaRepository.getMediaByTuneId(tuneId)
        return when {
            mediaEntity != null -> {
                val (title, subtitle) = mediaEntity.title.extractTextFromRoundBrackets()
                AudioItemDto(
                    parentUrl = mediaEntity.url,
                    directUrl = mediaEntity.directMediaUrl,
                    image = mediaEntity.imageUrl,
                    title = title,
                    subTitle = subtitle,
                    tuneId = mediaEntity.tuneId.orEmpty()
                )
            }

            else -> null
        }
    }
}