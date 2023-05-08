package com.alexeymerov.radiostations.domain.dto

data class CategoriesDto(
    val isHeader: Boolean = false,
    val url: String,
    val text: String,
    val image: String? = null,
    val currentTrack: String? = null,
    val isAudio: Boolean = false
)