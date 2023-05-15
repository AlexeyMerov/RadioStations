package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.data.remote.response.AudioBody
import com.alexeymerov.radiostations.data.remote.response.CategoryBody

interface RadioClient {

    suspend fun requestCategoriesByUrl(url: String): List<CategoryBody>

    suspend fun requestAudioByUrl(url: String): List<AudioBody>

}