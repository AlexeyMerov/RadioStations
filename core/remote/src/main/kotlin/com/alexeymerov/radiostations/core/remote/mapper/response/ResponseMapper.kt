package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.response.MainBody
import retrofit2.Response

interface ResponseMapper {

    fun <T> mapRadioResponseBody(body: Response<MainBody<T>>): List<T>

    fun <T> mapCountriesResponseBody(body: Response<List<T>>): List<T>

}