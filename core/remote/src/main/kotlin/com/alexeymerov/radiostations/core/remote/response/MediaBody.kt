package com.alexeymerov.radiostations.core.remote.response

import com.alexeymerov.radiostations.core.common.EMPTY
import com.squareup.moshi.Json

data class MediaBody(
    val url: String,
    val bitrate: Int = 0,
    @Json(name = "media_type")
    val mediaType: String = String.EMPTY,
) : ServerBodyType
