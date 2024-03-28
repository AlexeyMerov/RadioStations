package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.response.RadioMainBody
import io.ktor.client.statement.HttpResponse

interface ResponseMapper {

    suspend fun <T> mapRadioResponseBody(response: HttpResponse, body: RadioMainBody<T>?): List<T>

    suspend fun <T> mapCountriesResponseBody(response: HttpResponse, body: List<T>?): List<T>

}