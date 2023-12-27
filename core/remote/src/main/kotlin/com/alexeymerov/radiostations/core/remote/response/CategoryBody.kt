package com.alexeymerov.radiostations.core.remote.response

import com.alexeymerov.radiostations.core.common.EMPTY
import com.squareup.moshi.Json

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