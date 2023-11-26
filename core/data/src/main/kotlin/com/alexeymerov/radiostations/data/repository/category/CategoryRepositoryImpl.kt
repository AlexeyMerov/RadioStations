package com.alexeymerov.radiostations.data.repository.category


import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.core.common.BuildConfig
import com.alexeymerov.radiostations.data.mapper.category.CategoryMapper
import com.alexeymerov.radiostations.database.dao.CategoryDao
import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.remote.client.radio.RadioClient
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryMapper
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
        val categoryList = radioClient.requestCategoriesByUrl(parentUrl)
        val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoryList, parentUrl)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  inserting ${categoryEntities.size} entities")
        val favorites = categoryDao.getFavorites()

        favorites.forEach { favEntity ->
            categoryEntities.find { it.id == favEntity.id }?.let { it.isFavorite = true }
        }

        categoryDao.insertAll(categoryEntities)
    }

    /**
     * The server is not the best. To save initial values we using base url.
     * */
    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

}