package com.alexeymerov.radiostations.domain.dto


// we can use Result from Kotlin, but i believe it's a bit overkill
data class CategoryDto(
    val items: List<CategoryItemDto>,
    val isError: Boolean = false
)

/**
 * A simple class for to use in upper layers.
 *
 * Helps to avoid direct dependency between presentation and data layer.
 * To make it even better - we can add mapping on presentation layer as well to isolate all layers.
 * But it will add unnecessary runtime operations, so... up to you.
 * */
data class CategoryItemDto(
    val url: String,
    val originalText: String,
    val text: String,
    var subText: String? = null,
    val image: String? = null,
    val type: DtoItemType,
    var isFiltered: Boolean = false,
    var subItemsCount: Int = 0,
    var isFavorite: Boolean = false
)

enum class DtoItemType(val value: Int) {
    HEADER(0), CATEGORY(1), SUBCATEGORY(2), AUDIO(3)
}