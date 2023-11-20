package com.alexeymerov.radiostations.data.mapper.response

import com.alexeymerov.radiostations.data.remote.response.MainBody
import com.alexeymerov.radiostations.data.remote.response.ServerBodyType
import retrofit2.Response

interface ResponseMapper {

    fun <T : ServerBodyType> mapResponseBody(body: Response<MainBody<T>>): List<T>

}