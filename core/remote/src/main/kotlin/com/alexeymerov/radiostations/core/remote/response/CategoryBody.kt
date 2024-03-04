package com.alexeymerov.radiostations.core.remote.response

import com.alexeymerov.radiostations.core.common.EMPTY
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryBody(
    @SerialName("URL")
    val url: String? = null,
    val text: String,
    val type: String? = null,
    val image: String = String.EMPTY,
    @SerialName("current_track")
    val currentTrack: String = String.EMPTY,
    val children: List<CategoryBody>? = null
)