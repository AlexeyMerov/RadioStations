package com.alexeymerov.radiostations.core.data.repository.category

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getItemById(id: String): CategoryEntity

    fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>>

    suspend fun loadCategoriesByUrl(url: String)

}