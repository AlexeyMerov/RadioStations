package com.alexeymerov.radiostations.remote.client.radio

import com.alexeymerov.radiostations.common.BuildConfig
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.remote.api.RadioApi
import com.alexeymerov.radiostations.remote.client.BaseClient
import com.alexeymerov.radiostations.remote.response.CategoryBody
import com.alexeymerov.radiostations.remote.response.MediaBody
import javax.inject.Inject

class RadioClientImpl @Inject constructor(
    private val radioApi: RadioApi,
    private val responseMapper: ResponseMapper,
) : RadioClient, BaseClient<RadioApi>(radioApi) {

    override suspend fun requestCategoriesByUrl(url: String): List<CategoryBody> {
        //since server operates with links, we have to remove BASE_URL part before requests.
        val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
        val response = radioApi.getCategoriesByUrl(finalUrl)
        return responseMapper.mapResponseBody(response)
    }

    override suspend fun requestAudioByUrl(url: String): List<MediaBody> {
        //since server operates with links, we have to remove BASE_URL part before requests.
        val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
        val response = radioApi.getAudioByUrl(finalUrl)
        return responseMapper.mapResponseBody(response)
    }
}