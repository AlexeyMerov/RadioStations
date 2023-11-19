package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.data.remote.api.RadioApi
import com.alexeymerov.radiostations.data.remote.client.BaseClient
import com.alexeymerov.radiostations.data.remote.response.CategoryBody
import com.alexeymerov.radiostations.data.remote.response.MainBody
import com.alexeymerov.radiostations.data.remote.response.MediaBody
import retrofit2.Response
import javax.inject.Inject

class RadioClientImpl @Inject constructor(private val radioApi: RadioApi) : RadioClient, BaseClient<RadioApi>(radioApi) {

    override suspend fun requestCategoriesByUrl(url: String): Response<MainBody<CategoryBody>> {
        //since server operates with links, we have to remove BASE_URL part before requests.
        val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
        return radioApi.getCategoriesByUrl(finalUrl)
    }

    override suspend fun requestAudioByUrl(url: String): Response<MainBody<MediaBody>> {
        //since server operates with links, we have to remove BASE_URL part before requests.
        val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
        return radioApi.getAudioByUrl(finalUrl)
    }
}