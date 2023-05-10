package com.alexeymerov.radiostations.data.remote.response

import com.alexeymerov.radiostations.common.EMPTY
import com.squareup.moshi.Json

/**
 * Since server does not return an appropriate response, we bonded to use wrapper... 'good' old times.
 * */
data class ResponseWrapper(
    val head: Head,
    val body: List<ResponseBody>
)

data class Head(
    val status: String,
    val title: String? = null,
)

data class ResponseBody(
    @Json(name = "URL")
    val url: String? = null,
    val text: String,
    val type: String? = null,
    val image: String = String.EMPTY,
    @Json(name = "current_track")
    val currentTrack: String = String.EMPTY,
    val children: List<ResponseBody>? = null
)