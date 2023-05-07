package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.Cancelable
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity


interface CategoryUseCase : Cancelable {

    suspend fun getCategoriesByUrl(url: String): List<CategoryEntity>

}