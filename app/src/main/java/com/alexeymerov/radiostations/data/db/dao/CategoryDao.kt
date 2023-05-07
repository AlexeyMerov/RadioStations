package com.alexeymerov.radiostations.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity

@Dao
abstract class CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(list: List<CategoryEntity>)

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_PARENT_URL} = :parentUrl ORDER BY ${CategoryEntity.FIELD_TEXT} ASC")
    abstract fun getAllByParentUrl(parentUrl: String): List<CategoryEntity>

}