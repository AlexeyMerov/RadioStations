package com.alexeymerov.radiostations.domain.dto

/**
 * A simple class for to use in upper layers.
 *
 * Helps to avoid direct dependency between presentation and data layer.
 * To make it even better - we can add mapping on presentation layer as well to isolate all layers.
 * But it will add unnecessary runtime operations, so... up to you.
 * */
data class CategoriesDto(
    val isHeader: Boolean = false,
    val url: String,
    val text: String,
    val image: String? = null,
    val currentTrack: String? = null,
    val isAudio: Boolean = false
)