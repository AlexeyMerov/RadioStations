package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.common.extractTextFromRoundBrackets
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapper
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import javax.inject.Inject

class AudioUseCaseImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dtoCategoriesMapper: DtoCategoriesMapper
) : AudioUseCase {

    override suspend fun getMediaItem(url: String): AudioItemDto? {
        val mediaEntity = mediaRepository.getMediaByUrl(url)
        return when {
            mediaEntity != null -> {
                val (title, subtitle) = mediaEntity.title.extractTextFromRoundBrackets()
                AudioItemDto(
                    parentUrl = mediaEntity.url,
                    directUrl = mediaEntity.directMediaUrl,
                    image = mediaEntity.imageUrl,
                    title = title,
                    subTitle = subtitle
                )
            }

            else -> null
        }
    }

    override suspend fun getByUrl(url: String): CategoryItemDto? {
        val entity = mediaRepository.getItemByUrl(url) ?: return null
        return dtoCategoriesMapper.mapEntityToDto(entity)
    }

}