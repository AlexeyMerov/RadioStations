package com.alexeymerov.radiostations.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CountryEntity.TABLE_NAME)
data class CountryEntity(
    @PrimaryKey
    val tag: String,
    val nameEnglish: String,
    val nameNative: String,
    val phoneCode: String,
) {

    companion object {
        const val TABLE_NAME = "country"

        const val FIELD_TAG = "tag"
    }
}