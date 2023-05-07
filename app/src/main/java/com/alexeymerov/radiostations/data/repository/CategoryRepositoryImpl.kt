package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.mapper.CategoryMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClientImpl
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClientImpl,
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryMapper
) : CategoryRepository, BaseCoroutineScope() {

    override suspend fun getCategoriesByUrl(url: String): List<CategoryEntity> {
        val parentUrl = url.prepareUrl()
        return categoryDao.getAllByParentUrl(parentUrl)
    }

    override suspend fun loadCategoriesByUrl(url: String) {
        val parentUrl = url.prepareUrl()
        val categoriesResponse = radioClient.requestCategoriesByUrl(parentUrl)
        val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoriesResponse, parentUrl)
        categoryDao.insertAll(categoryEntities)
    }

    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.replace("http:", "https:")

}