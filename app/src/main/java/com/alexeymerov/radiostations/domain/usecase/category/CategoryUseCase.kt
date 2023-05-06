package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.Cancelable
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow


interface CategoryUseCase : Cancelable {
    suspend fun loadCategories()

    fun getCategories(): Flow<List<CategoryEntity>>
}