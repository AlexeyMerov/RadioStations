package com.alexeymerov.radiostations.core.remote.client.radio


import com.alexeymerov.radiostations.core.common.BuildConfig
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.remote.api.RadioApi
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.MediaBody
import javax.inject.Inject

class RadioClientImpl @Inject constructor(
    private val radioApi: RadioApi,
    private val responseMapper: ResponseMapper,
) : RadioClient {

    override suspend fun requestCategoriesByUrl(url: String): List<CategoryBody> {
        // there is an inconsistency with IDs and params and the reliable way is to use links from responses
        // but since server operates with links, we have to remove BASE_URL part before requests.
        val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
        val response = radioApi.getCategoriesByUrl(finalUrl)
        return responseMapper.mapRadioResponseBody(response)
    }

    override suspend fun requestAudioByUrl(url: String): MediaBody? {
        // there is an inconsistency with IDs and params and the reliable way is to use links from responses
        // but since server operates with links, we have to remove BASE_URL part before requests.
        val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
        val response = radioApi.getAudioByUrl(finalUrl)
        return responseMapper.mapRadioResponseBody(response).getOrNull(0)
    }
}