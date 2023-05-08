package com.alexeymerov.radiostations.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = StationEntity.TABLE_NAME,
    primaryKeys = [StationEntity.FIELD_PARENT_URL, StationEntity.FIELD_TEXT]
)
data class StationEntity(
    val url: String,
    val parentUrl: String,
    val text: String,
    val image: String,
    val currentTrack: String
) {

    companion object {
        const val TABLE_NAME = "station"
        const val FIELD_PARENT_URL = "parentUrl"
        const val FIELD_TEXT = "text"
    }
}