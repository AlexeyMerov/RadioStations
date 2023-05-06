package com.alexeymerov.radiostations.data.remote.response

import com.squareup.moshi.Json

data class ResponseWrapper(
    val head: Head,
    val body: List<ResponseBody>
)

data class Head(
    val status: String,
    val title: String
)

data class ResponseBody(
    @Json(name = "URL")
    val url: String,
    val element: String,
    val key: String,
    val text: String,
    val type: String
)