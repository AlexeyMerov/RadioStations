package com.alexeymerov.radiostations.domain.usecase.audio

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.data.local.db.entity.MediaEntity
import com.alexeymerov.radiostations.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AudioUseCaseImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dtoCategoriesMapper: DtoCategoriesMapper
) : AudioUseCase {

    override suspend fun getByUrl(url: String): CategoryItemDto {
        val entity = mediaRepository.getItemByUrl(url)
        return dtoCategoriesMapper.mapEntityToDto(entity)
    }

    override fun getFavorites(): Flow<CategoryDto> {
        return mediaRepository.getAllFavorites()
            .distinctUntilChanged { old, new -> old == new }
            .map { entityList ->
                if (entityList.isNotEmpty() && entityList[0].text == ERROR) {
                    return@map CategoryDto(emptyList(), isError = true)
                }

                val result = dtoCategoriesMapper.mapEntitiesToDto(entityList)
                return@map CategoryDto(result)
            }
    }

    override suspend fun toggleFavorite(item: CategoryItemDto) {
        mediaRepository.changeIsMediaFavorite(item.id, !item.isFavorite)
    }

    override suspend fun toggleFavorite(id: String) {
        val item = mediaRepository.getItemById(id)
        mediaRepository.changeIsMediaFavorite(id, !item.isFavorite)
    }

    override suspend fun unfavorite(item: CategoryItemDto) {
        mediaRepository.changeIsMediaFavorite(item.id, false)
    }

    override suspend fun getMediaItem(url: String): AudioItemDto? {
        val mediaEntity = mediaRepository.getMediaByUrl(url)
        return when {
            mediaEntity != null -> {
                val (title, subtitle) = dtoCategoriesMapper.extractLocationIfExist(mediaEntity.title)
                AudioItemDto(
                    parentUrl = mediaEntity.url,
                    directUrl = mediaEntity.directMediaUrl,
                    image = mediaEntity.imageUrl,
                    title = title,
                    subTitle = subtitle ?: String.EMPTY
                )
            }

            else -> null
        }
    }

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        return mediaRepository.getLastPlayingMediaItem()
            .map {
                if (it == null) return@map null

                AudioItemDto(
                    parentUrl = it.url,
                    directUrl = it.directMediaUrl,
                    image = it.imageUrl,
                    title = it.title,
                    subTitle = it.subtitle
                )
            }
    }

    override suspend fun setLastPlayingMediaItem(item: AudioItemDto?) {
        val body = if (item == null) null else {
            MediaEntity(
                url = item.parentUrl,
                directMediaUrl = item.directUrl,
                imageUrl = item.image,
                title = item.title,
                subtitle = item.subTitle
            )
        }

        mediaRepository.setLastPlayingMediaItem(body)
    }

    companion object {
        /**
         * At the moment server not returns any normal types to recognize an error,
         * so we just checking with hardcoded strings
         * */
        private const val ERROR = "No stations or shows available"
    }
}