package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.mapper.CategoryMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClientImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioCommunicator: RadioClientImpl,
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryMapper
) : CategoryRepository, BaseCoroutineScope() {

    override suspend fun loadCategories() {
        val categoriesResponse = radioCommunicator.loadCategories()
        val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoriesResponse)
        categoryDao.insertAll(categoryEntities)
    }

    override fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAll()
}