package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.data.remote.api.RadioApi
import com.alexeymerov.radiostations.data.remote.client.BaseClient
import com.alexeymerov.radiostations.data.remote.response.ResponseBody
import com.alexeymerov.radiostations.data.remote.response.ResponseWrapper
import timber.log.Timber
import javax.inject.Inject

class RadioClientImpl @Inject constructor(private val radioApi: RadioApi) : RadioClient, BaseClient<RadioApi>(radioApi) {

    override suspend fun requestCategoriesByUrl(url: String): List<ResponseBody> {
        return runCatching {
            //since server operates with links, we have to remove BASE_URL part before requests
            val finalUrl = url.replace(BuildConfig.BASE_URL, String.EMPTY)
            radioApi.getCategoriesByUrl(finalUrl)
        }
            .mapCatching { mapResponseBody(it) }
            .onFailure { Timber.e("response failed: $it") }
            .getOrDefault(emptyList())
    }

    private fun mapResponseBody(it: ResponseWrapper): List<ResponseBody> {
        if (it.head.status != STATUS_OK) {
            var errorText = it.head.title
            if (errorText.isNullOrEmpty()) errorText = "Something went wrong with request."
            throw Exception(errorText)
        }
        return it.body
    }

    private companion object {
        const val STATUS_OK = "200"
    }
}