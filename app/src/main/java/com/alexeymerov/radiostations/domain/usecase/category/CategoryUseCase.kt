package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.Cancelable
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import kotlinx.coroutines.flow.Flow


interface CategoryUseCase : Cancelable {

    fun getCategoriesByUrl(url: String): Flow<CategoryDto>

}