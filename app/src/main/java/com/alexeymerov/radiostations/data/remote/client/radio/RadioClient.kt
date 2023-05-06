package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.data.remote.response.ResponseBody

interface RadioClient {

    suspend fun loadCategories(): List<ResponseBody>

}