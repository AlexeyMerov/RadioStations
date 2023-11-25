package com.alexeymerov.radiostations.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = MediaEntity.TABLE_NAME)
data class MediaEntity(
    @PrimaryKey
    val id: Int = 0,
    val url: String,
    val directMediaUrl: String,
    val imageUrl: String,
    val title: String,
    val subtitle: String
) {

    companion object {
        const val TABLE_NAME = "media"

        const val FIELD_ID = "id"
    }
}

