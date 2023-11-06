package com.alexeymerov.radiostations.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexeymerov.radiostations.common.EMPTY
import com.squareup.moshi.Json

@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val position: Int, // For saving in DB but is wierd. There is a predefined sort for sure and no param for that as well.
    val url: String,
    val parentUrl: String,
    val text: String,
    val image: String = String.EMPTY,
    @Json(name = "current_track")
    val currentTrack: String = String.EMPTY,
    val type: EntityItemType,
    val childCount: Int? = null,
    val isFavorite: Boolean = false
) {

    companion object {
        const val TABLE_NAME = "category"

        const val FIELD_ID = "id"
        const val FIELD_URL = "url"
        const val FIELD_PARENT_URL = "parentUrl"
        const val FIELD_POSITION = "position"
        const val FIELD_TEXT = "text"
        const val FIELD_FAVORITE = "isFavorite"
    }
}

enum class EntityItemType {
    HEADER, CATEGORY, SUBCATEGORY, AUDIO
}