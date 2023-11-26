package com.alexeymerov.radiostations.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: MediaEntity)

    @Query("SELECT * FROM ${MediaEntity.TABLE_NAME} WHERE ${MediaEntity.FIELD_ID} = 0")
    abstract fun get(): Flow<MediaEntity?>

    @Query("DELETE FROM ${MediaEntity.TABLE_NAME}")
    abstract suspend fun clearTable()

}