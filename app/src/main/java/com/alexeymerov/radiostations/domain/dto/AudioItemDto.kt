package com.alexeymerov.radiostations.domain.dto

import com.alexeymerov.radiostations.common.EMPTY


// we can use Result from Kotlin
data class AudioItemDto(
    val url: String = String.EMPTY,
    val isError: Boolean = false
)