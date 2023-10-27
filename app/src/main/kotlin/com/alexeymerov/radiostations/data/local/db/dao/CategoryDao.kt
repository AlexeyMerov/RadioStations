package com.alexeymerov.radiostations.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(list: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: CategoryEntity)

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_PARENT_URL} = :url ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getAllByParentUrl(url: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_FAVORITE} = 1 ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getFavorites(): Flow<List<CategoryEntity>>

    @Query(
        "UPDATE ${CategoryEntity.TABLE_NAME} " +
            "SET ${CategoryEntity.FIELD_FAVORITE} = :isFavorite " +
            "WHERE ${CategoryEntity.FIELD_URL} = :itemUrl AND ${CategoryEntity.FIELD_TEXT} = :itemText"
    )
    abstract suspend fun setStationFavorite(itemUrl: String, itemText: String, isFavorite: Int)
}