package com.alexeymerov.radiostations.core.remote.response

import com.alexeymerov.radiostations.core.common.EMPTY
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaBody(
    val url: String,
    val bitrate: Int = 0,
    @SerialName("media_type")
    val mediaType: String = String.EMPTY,
)
