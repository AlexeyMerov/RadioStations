package com.alexeymerov.radiostations.core.remote.mapper.response

import com.alexeymerov.radiostations.core.remote.response.RadioMainBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import timber.log.Timber
import javax.inject.Inject

class ResponseMapperImpl @Inject constructor() : ResponseMapper {

    //no error handling at the moment since uncertainty of server errors and response format
    override suspend fun <T> mapRadioResponseBody(response: HttpResponse, body: RadioMainBody<T>?): List<T> {
        var errorText: String? = null
        var resultList = emptyList<T>()

        when {
            !response.status.isSuccess() -> errorText = response.status.description
            body == null -> errorText = "Response body is null"
            body.head.status != HttpStatusCode.OK.value.toString() -> errorText = body.head.title ?: "Response status: ${body.head.status}"
            else -> resultList = body.body
        }

        if (errorText != null) Timber.d("mapRadioResponseBody $errorText")

        return resultList
    }


    override suspend fun <T> mapCountriesResponseBody(response: HttpResponse, body: List<T>?): List<T> {
        var errorText: String? = null
        var resultList = emptyList<T>()

        when {
            !response.status.isSuccess() -> errorText = response.status.description
            body == null -> errorText = "Response body is null"
            body.isEmpty() -> errorText = "Response body is empty"
            else -> resultList = body
        }

        if (errorText != null) Timber.d("mapCountriesResponseBody $errorText")

        return resultList
    }
}