package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.data.remote.response.AudioBody
import com.alexeymerov.radiostations.data.remote.response.CategoryBody
import com.alexeymerov.radiostations.data.remote.response.MainBody
import retrofit2.Response

interface RadioClient {

    suspend fun requestCategoriesByUrl(url: String): Response<MainBody<CategoryBody>>

    suspend fun requestAudioByUrl(url: String): Response<MainBody<AudioBody>>

}