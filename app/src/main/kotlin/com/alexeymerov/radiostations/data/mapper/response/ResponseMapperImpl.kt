package com.alexeymerov.radiostations.data.mapper.response

import com.alexeymerov.radiostations.data.remote.response.MainBody
import com.alexeymerov.radiostations.data.remote.response.ServerBodyType
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class ResponseMapperImpl @Inject constructor() : ResponseMapper {

    //no error handling at the moment since uncertainty of server errors and response format
    override fun <T : ServerBodyType> mapResponseBody(body: Response<MainBody<T>>): List<T> {
        var errorText: String? = null
        var resultList = emptyList<T>()
        val mainBody = body.body()
        when {
            !body.isSuccessful -> errorText = body.message()
            mainBody == null -> errorText = "Response body is null"
            mainBody.head.status != Companion.STATUS_OK -> errorText = mainBody.head.title ?: "Response status: ${mainBody.head.status}"
            else -> resultList = mainBody.body
        }

        if (errorText != null) Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] $errorText")

        return resultList
    }

    companion object {
        const val STATUS_OK = "200"
    }
}