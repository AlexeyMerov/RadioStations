package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import kotlinx.coroutines.delay

class FakeAudioUseCase : AudioUseCase {

    var delay = 0L

    override suspend fun getMediaItem(url: String): AudioItemDto? {
        delay(delay)
        val validData = AudioItemDto(
            parentUrl = "parenturl",
            directUrl = "directUrl",
            image = "image",
            title = "title",
            subTitle = "subTitle"
        )
        return when (url) {
            VALID_URL, VALID_URL_IS_FAVORITE -> validData
            VALID_URL_NO_SUBTITLE -> validData.copy(subTitle = null)
            else -> null
        }
    }

    override suspend fun getByUrl(url: String): CategoryItemDto? {
        val validItem = CategoryItemDto(
            id = "id",
            url = "url",
            subTitle = "Hello",
            text = "Station Name",
            type = DtoItemType.AUDIO,
            initials = "HA",
            isFavorite = false
        )
        return when (url) {
            VALID_URL -> validItem
            VALID_URL_IS_FAVORITE -> validItem.copy(isFavorite = true)
            else -> null

        }
    }

    companion object {
        const val VALID_URL = "valid_url"
        const val VALID_URL_NO_SUBTITLE = "valid_url_no_subtitle"
        const val VALID_URL_IS_FAVORITE = "valid_url_is_favorite"
    }
}