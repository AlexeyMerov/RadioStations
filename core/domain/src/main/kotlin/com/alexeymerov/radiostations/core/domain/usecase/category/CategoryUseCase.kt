package com.alexeymerov.radiostations.core.domain.usecase.category

import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import kotlinx.coroutines.flow.Flow


interface CategoryUseCase {

    fun getAllByUrl(url: String): Flow<CategoryDto>

    suspend fun getByTuneId(tuneId: String): CategoryItemDto?

    suspend fun loadCategoriesByUrl(url: String)

}