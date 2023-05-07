package com.alexeymerov.radiostations.data.remote.api

import com.alexeymerov.radiostations.data.remote.response.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Url

interface RadioApi {

    @GET
    suspend fun getCategoriesByUrl(@Url fullUrl: String): ResponseWrapper


}