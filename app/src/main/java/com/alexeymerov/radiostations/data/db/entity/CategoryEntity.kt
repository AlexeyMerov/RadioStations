package com.alexeymerov.radiostations.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey
    val text: String,
    val url: String,
    val key: String
) {

    companion object {
        const val TABLE_NAME = "category"
    }
}