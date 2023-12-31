package com.alexeymerov.radiostations.core.remote.api

import com.alexeymerov.radiostations.core.remote.response.CountryBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CountryApi {

    @GET("all")
    suspend fun getAllCountries(
        @Query("fields") fields: String
    ): Response<List<CountryBody>>

}