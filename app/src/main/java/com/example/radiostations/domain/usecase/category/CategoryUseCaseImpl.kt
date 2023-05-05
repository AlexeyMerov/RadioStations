package com.example.radiostations.domain.usecase.category

import com.example.radiostations.common.BaseCoroutineScope
import com.example.radiostations.data.repository.CategoryRepository
import javax.inject.Inject

class CategoryUseCaseImpl @Inject constructor(private val categoryRepository: CategoryRepository) : CategoryUseCase, BaseCoroutineScope() {

    override suspend fun loadCategories() {
        categoryRepository.loadCategories()
    }
}