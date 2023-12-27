package com.alexeymerov.radiostations.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import kotlinx.coroutines.flow.Flow


@Dao
abstract class CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<CountryEntity>)

    @Query("SELECT * FROM ${CountryEntity.TABLE_NAME}")
    abstract fun getAll(): Flow<List<CountryEntity>>

    @Query("SELECT COUNT(${CountryEntity.FIELD_TAG}) FROM ${CountryEntity.TABLE_NAME}")
    abstract fun size(): Int

}