package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.Cancelable


interface CategoryUseCase : Cancelable {
    suspend fun loadCategories()
}