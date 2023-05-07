package com.alexeymerov.radiostations.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey
    val url: String,
    val parentUrl: String,
    val text: String,
    val key: String
) {

    companion object {
        const val TABLE_NAME = "category"
        const val FIELD_PARENT_URL = "parentUrl"
        const val FIELD_TEXT = "text"
    }
}