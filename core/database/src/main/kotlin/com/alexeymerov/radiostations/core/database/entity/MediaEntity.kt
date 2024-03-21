package com.alexeymerov.radiostations.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = MediaEntity.TABLE_NAME)
data class MediaEntity(
    @PrimaryKey
    val id: Int = 0,
    val url: String,
    val directMediaUrl: String,
    val imageUrl: String,
    val imageBase64: String? = null,
    val title: String,
    val subtitle: String,
    val tuneId: String? = null,
) {

    companion object {
        const val TABLE_NAME = "media"

        const val FIELD_ID = "id"
    }
}

