package com.alexeymerov.radiostations.data.remote.response

import com.alexeymerov.radiostations.common.EMPTY
import com.squareup.moshi.Json

interface ServerBodyType

/**
 * Since server does not return an appropriate response, we bonded to use wrapper... 'good' old times.
 * */
data class ResponseWrapper<T : ServerBodyType>(
    val head: Head,
    val body: List<T>
)

data class Head(
    val status: String,
    val title: String? = null,
)

data class CategoryBody(
    @Json(name = "URL")
    val url: String? = null,
    val text: String,
    val type: String? = null,
    val image: String = String.EMPTY,
    @Json(name = "current_track")
    val currentTrack: String = String.EMPTY,
    val children: List<CategoryBody>? = null
) : ServerBodyType

data class AudioBody(
    val url: String? = null,
    val bitrate: Int = 0,
    @Json(name = "media_type")
    val mediaType: String = String.EMPTY,
) : ServerBodyType

