package com.alexeymerov.radiostations.core.remote.api

import com.alexeymerov.radiostations.core.remote.response.CategoryBody
import com.alexeymerov.radiostations.core.remote.response.MainBody
import com.alexeymerov.radiostations.core.remote.response.MediaBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RadioApi {

    /**
     * Server operates with full URLs, that's why we don't use a QUERY params
     * */
    @GET
    suspend fun getCategoriesByUrl(@Url fullUrl: String): Response<MainBody<CategoryBody>>

    @GET
    suspend fun getAudioByUrl(@Url fullUrl: String): Response<MainBody<MediaBody>>

}