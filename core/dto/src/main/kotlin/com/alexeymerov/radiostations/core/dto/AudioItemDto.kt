package com.alexeymerov.radiostations.core.dto


data class AudioItemDto(
    val parentUrl: String,
    val directUrl: String,
    val image: String,
    val imageBase64: String? = null,
    val title: String,
    val subTitle: String? = null,
    val tuneId: String
)