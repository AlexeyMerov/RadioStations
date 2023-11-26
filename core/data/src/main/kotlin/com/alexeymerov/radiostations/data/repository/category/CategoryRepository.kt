package com.alexeymerov.radiostations.data.repository.category

import com.alexeymerov.radiostations.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getItemById(id: String): CategoryEntity

    fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>>

    suspend fun loadCategoriesByUrl(url: String)

}