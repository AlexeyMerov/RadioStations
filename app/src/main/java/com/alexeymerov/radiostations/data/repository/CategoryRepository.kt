package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun loadCategories()

    fun getCategories(): Flow<List<CategoryEntity>>

}