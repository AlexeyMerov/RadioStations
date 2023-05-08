package com.alexeymerov.radiostations.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = CategoryEntity.TABLE_NAME,
    primaryKeys = [CategoryEntity.FIELD_PARENT_URL, CategoryEntity.FIELD_TEXT]
)
data class CategoryEntity(
    val url: String,
    val parentUrl: String,
    val text: String,
    val key: String? = null,
    val isHeader: Boolean = false
) {

    companion object {
        const val TABLE_NAME = "category"
        const val FIELD_PARENT_URL = "parentUrl"
        const val FIELD_TEXT = "text"
    }
}