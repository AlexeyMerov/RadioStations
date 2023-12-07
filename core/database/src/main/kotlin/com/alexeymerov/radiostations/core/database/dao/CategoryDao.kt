package com.alexeymerov.radiostations.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_ID} = :id")
    abstract suspend fun getById(id: String): CategoryEntity

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_URL} = :url")
    abstract suspend fun getByUrl(url: String): CategoryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(list: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: CategoryEntity)

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_PARENT_URL} = :url ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getAllByParentUrl(url: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_FAVORITE} = 1 ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getFavoritesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_FAVORITE} = 1 ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getFavorites(): List<CategoryEntity>

    @Query(
        "UPDATE ${CategoryEntity.TABLE_NAME} " +
            "SET ${CategoryEntity.FIELD_FAVORITE} = :isFavorite " +
            "WHERE ${CategoryEntity.FIELD_ID} = :itemId"
    )
    abstract suspend fun setStationFavorite(itemId: String, isFavorite: Int)
}