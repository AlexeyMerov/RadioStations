package com.alexeymerov.radiostations.domain.dto


data class AudioItemDto(
    val parentUrl: String,
    val directUrl: String,
    val image: String,
    val title: String,
    val subTitle: String
)