package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.repository.CategoryRepository
import javax.inject.Inject

class CategoryUseCaseImpl @Inject constructor(private val categoryRepository: CategoryRepository) : CategoryUseCase, BaseCoroutineScope() {

    override suspend fun loadCategories() {
        categoryRepository.loadCategories()
    }
}