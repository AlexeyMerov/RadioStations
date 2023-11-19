package com.alexeymerov.radiostations.data.repository.category

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.local.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.mapper.EntityCategoryMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.data.repository.mapResponseBody
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
    private val categoryMapper: EntityCategoryMapper
) : CategoryRepository {

    override suspend fun getItemById(id: String): CategoryEntity = categoryDao.getById(id)

    override fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>> {
        return categoryDao.getAllByParentUrl(url.prepareUrl())
    }

    /**
     * The server is not the best. So we use URL as only reliable parameter to operate with.
     * */
    override suspend fun loadCategoriesByUrl(url: String) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  request new data")
        val parentUrl = url.prepareUrl()
        val categoriesResponse = radioClient.requestCategoriesByUrl(parentUrl)
        val categoryList = mapResponseBody(categoriesResponse)
        val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoryList, parentUrl)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  inserting ${categoryEntities.size} entities")
        categoryDao.insertAll(categoryEntities)
    }

    /**
     * The server is not the best. To save initial values we using base url.
     * */
    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

}