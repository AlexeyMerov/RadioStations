package com.alexeymerov.radiostations.core.database.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_ID} = :id")
    abstract suspend fun getById(id: String): CategoryEntity

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_URL} = :url")
    abstract suspend fun getByUrl(url: String): CategoryEntity

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_PARENT_URL} = :url ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getAllByParentUrl(url: String): Flow<List<CategoryEntity>>

    @Query("SELECT ${CategoryEntity.FIELD_ID} FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_PARENT_URL} = :url")
    abstract fun getAllIdsByParentUrl(url: String): List<String>

    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_FAVORITE} = 1 ORDER BY ${CategoryEntity.FIELD_POSITION} ASC")
    abstract fun getFavoritesFlow(): Flow<List<CategoryEntity>>

    @Upsert
    abstract suspend fun insertAll(list: List<CategoryEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateAll(list: List<CategoryEntity>)

    @Query("DELETE FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.FIELD_ID} IN (:ids)")
    abstract suspend fun removeAllByIds(ids: List<String>)

    @Query(
        "UPDATE ${CategoryEntity.TABLE_NAME} " +
            "SET ${CategoryEntity.FIELD_FAVORITE} = :isFavorite " +
            "WHERE ${CategoryEntity.FIELD_ID} = :itemId"
    )
    abstract suspend fun setStationFavorite(itemId: String, isFavorite: Int)
}