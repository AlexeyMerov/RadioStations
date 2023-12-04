package com.alexeymerov.radiostations.core.remote.client.radio

import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.MediaBody

interface RadioClient {

    suspend fun requestCategoriesByUrl(url: String): List<CategoryBody>

    suspend fun requestAudioByUrl(url: String): MediaBody?

}