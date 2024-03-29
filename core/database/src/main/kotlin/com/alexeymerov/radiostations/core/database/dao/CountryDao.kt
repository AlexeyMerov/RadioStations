package com.alexeymerov.radiostations.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.core.database.entity.CountryEntity


@Dao
abstract class CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<CountryEntity>)

    @Query("SELECT * FROM ${CountryEntity.TABLE_NAME} ORDER BY ${CountryEntity.FIELD_NAME} ASC")
    abstract fun getAllForTest(): List<CountryEntity>

    @Query("SELECT * FROM ${CountryEntity.TABLE_NAME} ORDER BY ${CountryEntity.FIELD_NAME} ASC")
    abstract fun getAll(): PagingSource<Int, CountryEntity>

    @Query(
        "SELECT * FROM ${CountryEntity.TABLE_NAME} " +
            "WHERE ${CountryEntity.FIELD_NAME} LIKE '%' || :text || '%' " +
            "OR ${CountryEntity.FIELD_NAME_NATIVE} LIKE '%' || :text || '%' " +
            "OR ${CountryEntity.FIELD_PHONE_CODE} LIKE '%' || :text || '%' " +
            "ORDER BY ${CountryEntity.FIELD_NAME} ASC"
    )
    abstract fun searchByText(text: String): PagingSource<Int, CountryEntity>

    @Query("SELECT COUNT(${CountryEntity.FIELD_TAG}) FROM ${CountryEntity.TABLE_NAME}")
    abstract fun size(): Int

    @Query("DELETE FROM ${CountryEntity.TABLE_NAME}")
    abstract suspend fun removeAll()

}