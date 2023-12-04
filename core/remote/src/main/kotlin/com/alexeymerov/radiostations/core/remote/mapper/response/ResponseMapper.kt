package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.response.MainBody
import com.alexeymerov.radiostations.core.remote.response.ServerBodyType
import retrofit2.Response

interface ResponseMapper {

    fun <T : ServerBodyType> mapResponseBody(body: Response<MainBody<T>>): List<T>

}