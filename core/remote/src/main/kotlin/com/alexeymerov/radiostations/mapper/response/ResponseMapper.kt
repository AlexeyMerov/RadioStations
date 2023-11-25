package com.alexeymerov.radiostations.mapper.response

import com.alexeymerov.radiostations.remote.response.MainBody
import com.alexeymerov.radiostations.remote.response.ServerBodyType
import retrofit2.Response

interface ResponseMapper {

    fun <T : ServerBodyType> mapResponseBody(body: Response<MainBody<T>>): List<T>

}