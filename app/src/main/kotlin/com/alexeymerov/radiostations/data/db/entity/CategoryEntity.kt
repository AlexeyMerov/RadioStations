package com.alexeymerov.radiostations.data.db.entity

import androidx.room.Entity
import com.alexeymerov.radiostations.common.EMPTY
import com.squareup.moshi.Json

@Entity(
    tableName = CategoryEntity.TABLE_NAME,
    primaryKeys = [CategoryEntity.FIELD_PARENT_URL, CategoryEntity.FIELD_TEXT]
)
data class CategoryEntity(
    val position: Int, // For saving in DB but is wierd. There is a predefined sort for sure and no param for that as well.
    val url: String,
    val parentUrl: String,
    val text: String,
    val image: String = String.EMPTY,
    @Json(name = "current_track")
    val currentTrack: String = String.EMPTY,
    val type: EntityItemType,
    val childCount: Int? = null
) {

    companion object {
        const val TABLE_NAME = "category"

        const val FIELD_PARENT_URL = "parentUrl"
        const val FIELD_POSITION = "position"
        const val FIELD_TEXT = "text"
    }
}

enum class EntityItemType {
    HEADER, CATEGORY, SUBCATEGORY, AUDIO
}