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
import timber.log.Timber
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

    /**
     * The server is bad boy. So we use URL as only reliable parameter to operate with.
     *
     * */
    override fun loadCategoriesByUrl(url: String) {
        launch {
            val parentUrl = url.prepareUrl()
            val categoriesResponse = radioClient.requestCategoriesByUrl(parentUrl)
            Timber.d("request new data")

            // we don't know about nested children until make a request
            val hasChildren = categoriesResponse.firstOrNull { it.children != null } != null
            Timber.d("response from server has children: $hasChildren")
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

    /**
     * In case there are no audio station after mapping, we saving only category.
     * */
    private suspend fun processCategoriesWithStations(categoriesResponse: List<ResponseBody>, parentUrl: String) {
        val categoryAndStationsMap = categoryMapper.mapCategoryWithStationsResponseToMap(categoriesResponse, parentUrl)
        categoryAndStationsMap.forEach { (category, stations) ->
            categoryDao.insert(category)
            Timber.d("no audio in category: $stations != null")
            if (stations != null) stationDao.insertAll(stations)
        }
    }

    private suspend fun processOnlyCategories(categoriesResponse: List<ResponseBody>, parentUrl: String) {
        val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoriesResponse, parentUrl)
        categoryDao.insertAll(categoryEntities)
    }

    /**
     * The server is bad boy. To save initial values we using base url.
     * */
    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

}