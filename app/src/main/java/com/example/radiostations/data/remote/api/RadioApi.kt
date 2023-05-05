package com.example.radiostations.data.remote.api

import com.example.radiostations.data.remote.response.ResponseWrapper
import retrofit2.http.GET

interface RadioApi {

    @GET("?render=json")
    suspend fun getCategories(): ResponseWrapper

}