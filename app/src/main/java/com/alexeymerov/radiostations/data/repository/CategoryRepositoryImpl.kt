package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.dao.StationDao
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.data.mapper.EntityCategoryMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.data.remote.response.ResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
    private val stationDao: StationDao,
    private val categoryMapper: EntityCategoryMapper
) : CategoryRepository, BaseCoroutineScope() {

    override fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>> {
        val parentUrl = url.prepareUrl()
        return categoryDao.getAllByParentUrl(parentUrl)
    }

    override fun loadCategoriesByUrl(url: String) {
        launch {
            val parentUrl = url.prepareUrl()
            val categoriesResponse = radioClient.requestCategoriesByUrl(parentUrl)

            val hasChildren = categoriesResponse.firstOrNull { it.children != null } != null
            if (hasChildren) {
                processCategoriesWithStations(categoriesResponse, parentUrl)
            } else {
                processOnlyCategories(categoriesResponse, parentUrl)
            }
        }
    }

    override suspend fun getStationsByCategory(entity: CategoryEntity): List<StationEntity> {
        val url = categoryMapper.mapUrlForStation(entity.parentUrl, entity)
        return stationDao.getAllByParentUrl(url)
    }

    private suspend fun processCategoriesWithStations(categoriesResponse: List<ResponseBody>, parentUrl: String) {
        val categoryAndStationsMap = categoryMapper.mapCategoryWithStationsResponseToMap(categoriesResponse, parentUrl)
        categoryAndStationsMap.forEach { (category, stations) ->
            categoryDao.insert(category)
            if (stations != null) stationDao.insertAll(stations)
        }
    }

    private suspend fun processOnlyCategories(categoriesResponse: List<ResponseBody>, parentUrl: String) {
        val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoriesResponse, parentUrl)
        categoryDao.insertAll(categoryEntities)
    }

    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

}