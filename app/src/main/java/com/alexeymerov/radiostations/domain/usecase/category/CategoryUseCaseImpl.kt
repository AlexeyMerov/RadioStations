package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryUseCaseImpl @Inject constructor(private val categoryRepository: CategoryRepository) : CategoryUseCase, BaseCoroutineScope() {

    override suspend fun loadCategories() = categoryRepository.loadCategories()

    override fun getCategories(): Flow<List<CategoryEntity>> = categoryRepository.getCategories()
}