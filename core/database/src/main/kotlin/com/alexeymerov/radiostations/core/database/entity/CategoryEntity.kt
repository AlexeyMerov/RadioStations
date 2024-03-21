package com.alexeymerov.radiostations.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexeymerov.radiostations.core.common.EMPTY

@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey
    val id: String, // generating manually
    val position: Int, // For saving in DB but is wierd. There is a predefined sort for sure and no param for that as well.
    val url: String,
    val parentUrl: String,
    val text: String,
    val subTitle: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val image: String = String.EMPTY,
    val currentTrack: String = String.EMPTY,
    val type: EntityItemType,
    val childCount: Int? = null,
    var isFavorite: Boolean = false,
    var tuneId: String? = null
) {

    companion object {
        const val TABLE_NAME = "category"

        const val FIELD_ID = "id"
        const val FIELD_URL = "url"
        const val FIELD_PARENT_URL = "parentUrl"
        const val FIELD_POSITION = "position"
        const val FIELD_FAVORITE = "isFavorite"
        const val FIELD_TUNE_ID = "tuneId"
    }
}

enum class EntityItemType {
    HEADER, CATEGORY, SUBCATEGORY, AUDIO
}