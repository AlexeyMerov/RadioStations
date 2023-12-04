package com.alexeymerov.radiostations.core.domain.usecase.category

import com.alexeymerov.radiostations.core.dto.CategoryDto
import kotlinx.coroutines.flow.Flow


interface CategoryUseCase {

    fun getAllByUrl(url: String): Flow<CategoryDto>

    suspend fun loadCategoriesByUrl(url: String)

}