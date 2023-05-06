package com.alexeymerov.radiostations.data.remote.api

import com.alexeymerov.radiostations.data.remote.response.ResponseWrapper
import retrofit2.http.GET

interface RadioApi {

    @GET("/")
    suspend fun getCategories(): ResponseWrapper

}