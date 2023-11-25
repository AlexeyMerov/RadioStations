package com.alexeymerov.radiostations.remote.client.radio

import com.alexeymerov.radiostations.remote.response.CategoryBody
import com.alexeymerov.radiostations.remote.response.MediaBody

interface RadioClient {

    suspend fun requestCategoriesByUrl(url: String): List<CategoryBody>

    suspend fun requestAudioByUrl(url: String): List<MediaBody>

}