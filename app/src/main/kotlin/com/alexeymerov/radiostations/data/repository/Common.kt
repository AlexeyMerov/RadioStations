package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.data.remote.response.MainBody
import com.alexeymerov.radiostations.data.remote.response.ServerBodyType
import retrofit2.Response
import timber.log.Timber

//no error handling at the moment since uncertainty of server errors and response format
fun <T : ServerBodyType> mapResponseBody(body: Response<MainBody<T>>): List<T> {
    var errorText: String? = null
    var resultList = emptyList<T>()
    val mainBody = body.body()
    when {
        !body.isSuccessful -> errorText = body.message()
        mainBody == null -> errorText = "Response body is null"
        mainBody.head.status != STATUS_OK -> errorText = mainBody.head.title ?: "Response status: ${mainBody.head.status}"
        else -> resultList = mainBody.body
    }

    if (errorText != null) Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] $errorText")

    return resultList
}

const val STATUS_OK = "200"