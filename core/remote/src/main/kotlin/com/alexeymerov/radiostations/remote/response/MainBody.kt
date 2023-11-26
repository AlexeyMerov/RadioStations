package com.alexeymerov.radiostations.remote.response

import com.alexeymerov.radiostations.common.EMPTY
import com.squareup.moshi.Json

interface ServerBodyType

/**
 * Since server does not return an appropriate response, we bonded to use wrapper... 'good' old times.
 * */
data class MainBody<T : ServerBodyType>(
    val head: HeadBody,
    val body: List<T>
)

data class HeadBody(
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

data class MediaBody(
    val url: String,
    val bitrate: Int = 0,
    @Json(name = "media_type")
    val mediaType: String = String.EMPTY,
) : ServerBodyType

