package com.alexeymerov.radiostations.core.dto


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
 *
 * @param id - unique id of the item
 * @param url - url to load next screen of any item but Header
 * @param text - main text to display for any type
 * @param subTitle - additional info OR location name of the station
 * @param image - image url for stations
 * @param type - type of item @see [DtoItemType]
 * @param isFiltered - is item filtered by header by user
 * @param subItemsCount - count of sub items for header
 * @param isFavorite - is item favorite state
 * @param initials - initials of item for image placeholder
 * @param absoluteIndex - index of item in the list of all items to scroll to it
 * */
data class CategoryItemDto(
    val id: String,
    val url: String,
    val text: String,
    var subTitle: String? = null,
    val image: String? = null,
    val type: DtoItemType,
    var isFiltered: Boolean = false,
    var subItemsCount: Int = 0,
    var isFavorite: Boolean = false,
    var initials: String,
    var absoluteIndex: Int = 0,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var tuneId: String? = null
) {
    fun hasLocation(): Boolean {
        return latitude != null && longitude != null
    }
}

enum class DtoItemType(val value: Int) {
    HEADER(0), CATEGORY(1), SUBCATEGORY(2), AUDIO(3)
}