package com.alexeymerov.radiostations.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.data.db.entity.StationEntity

@Dao
abstract class StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(list: List<StationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: StationEntity)

    @Query("SELECT * FROM ${StationEntity.TABLE_NAME} WHERE ${StationEntity.FIELD_PARENT_URL} = :url")
    abstract suspend fun getAllByParentUrl(url: String): List<StationEntity>

}