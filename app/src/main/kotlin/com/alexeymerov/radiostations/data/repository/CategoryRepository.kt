package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.remote.response.AudioBody
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getItemById(id: String): CategoryEntity

    fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>>

    fun getFavorites(): Flow<List<CategoryEntity>>

    suspend fun loadCategoriesByUrl(url: String)

    suspend fun getAudioByUrl(url: String): AudioBody? //todo null is ugly. think about some generic response type (mb kotlin's Result)

    suspend fun changeStationFavorite(itemId: String, isFavorite: Boolean)

}