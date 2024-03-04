package com.alexeymerov.radiostations.core.dto


data class AudioItemDto(
    val parentUrl: String,
    val directUrl: String,
    val image: String,
    val title: String,
    val subTitle: String?
)