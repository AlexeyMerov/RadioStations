package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.repository.CategoryRepository
import javax.inject.Inject

class CategoryUseCaseImpl @Inject constructor(private val categoryRepository: CategoryRepository) : CategoryUseCase, BaseCoroutineScope() {

    override suspend fun getCategoriesByUrl(url: String): List<CategoryEntity> {
        categoryRepository.loadCategoriesByUrl(url)
        return categoryRepository.getCategoriesByUrl(url)
    }

    override fun cancelJobs() {
        super.cancelJobs()
        categoryRepository.cancelJobs()
    }
}