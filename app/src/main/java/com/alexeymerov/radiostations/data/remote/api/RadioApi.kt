package com.alexeymerov.radiostations.data.remote.api

import com.alexeymerov.radiostations.data.remote.response.AudioBody
import com.alexeymerov.radiostations.data.remote.response.CategoryBody
import com.alexeymerov.radiostations.data.remote.response.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Url

interface RadioApi {

    /**
     * Server operates with full URLs, that's why we don't use a QUERY params
     * */
    @GET
    suspend fun getCategoriesByUrl(@Url fullUrl: String): ResponseWrapper<CategoryBody>

    @GET
    suspend fun getAudioByUrl(@Url fullUrl: String): ResponseWrapper<AudioBody>


}