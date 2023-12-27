package com.alexeymerov.radiostations.core.remote.api

import com.alexeymerov.radiostations.core.remote.response.CountryBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET

interface CountryApi {

    @GET("all")
    suspend fun getAllCountries(
        @Field("fields") fields: String
    ): Response<List<CountryBody>>

}