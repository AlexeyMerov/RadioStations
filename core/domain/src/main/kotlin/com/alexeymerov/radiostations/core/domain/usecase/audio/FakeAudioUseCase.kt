package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.dto.AudioItemDto
import kotlinx.coroutines.delay

class FakeAudioUseCase : AudioUseCase {

    var delay = 0L

    override suspend fun getMediaItem(tuneId: String): AudioItemDto? {
        delay(delay)
        val validData = AudioItemDto(
            parentUrl = "parenturl",
            directUrl = "directUrl",
            image = "image",
            title = "title",
            subTitle = "subTitle",
            tuneId = "tuneId"
        )
        return when (tuneId) {
            VALID_ID, VALID_ID_IS_FAVORITE -> validData
            VALID_ID_NO_SUBTITLE -> validData.copy(subTitle = null)
            else -> null
        }
    }

    companion object {
        const val VALID_ID = "VALID_ID"
        const val VALID_ID_NO_SUBTITLE = "VALID_ID_NO_SUBTITLE"
        const val VALID_ID_IS_FAVORITE = "VALID_ID_IS_FAVORITE"
    }
}