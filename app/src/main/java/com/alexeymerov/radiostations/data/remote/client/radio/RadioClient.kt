package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.data.remote.response.ResponseBody

interface RadioClient {

    suspend fun requestCategoriesByUrl(url: String): List<ResponseBody>

}