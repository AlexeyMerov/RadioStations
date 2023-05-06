package com.alexeymerov.radiostations.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {

    @Insert
    abstract suspend fun insertAll(list: List<CategoryEntity>)

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME}")
    abstract fun getAll(): Flow<List<CategoryEntity>>

}