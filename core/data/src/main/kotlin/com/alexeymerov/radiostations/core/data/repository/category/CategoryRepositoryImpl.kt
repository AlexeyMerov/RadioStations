package com.alexeymerov.radiostations.core.data.repository.category


import com.alexeymerov.radiostations.core.common.BuildConfig
import com.alexeymerov.radiostations.core.common.httpsEverywhere
import com.alexeymerov.radiostations.core.data.mapper.category.CategoryMapper
import com.alexeymerov.radiostations.core.data.mapper.geocoder.LocationGeocoder
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryMapper,
    private val locationGeocoder: LocationGeocoder
) : CategoryRepository {

    override fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>> {
        return categoryDao.getAllByParentUrl(url.prepareUrl())
    }

    /**
     * Get new items -> Remove excess from DB -> Saving only fresh new ones -> Updating location if needed
     * The server is not the best. So we use URL as only reliable parameter to operate with.
     * */
    override suspend fun loadCategoriesByUrl(url: String) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  request new data")
        val parentUrl = url.prepareUrl()
        val responseCategoryList = radioClient.requestCategoriesByUrl(parentUrl)
        var newCategoryEntities = categoryMapper.mapCategoryResponseToEntity(responseCategoryList, parentUrl)
        val newCategoryEntitiesIds = newCategoryEntities.map { it.id }
        var savedItemsIds = categoryDao.getAllIdsByParentUrl(parentUrl)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  get ${newCategoryEntities.size} new entities")

        savedItemsIds = removeExcessItems(savedItemsIds, newCategoryEntitiesIds)

        // location is not provided by server and we're generating it manually on device,
        // we don't want to remove items with location since it's a long operation
        val freshNewItemsIds = newCategoryEntitiesIds.filterNot { it in savedItemsIds }
        newCategoryEntities = newCategoryEntities.filter { it.id in freshNewItemsIds }
        categoryDao.insertAll(newCategoryEntities)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  saved ${newCategoryEntities.size} new entities")

        // ugly hardcode to call location mapping only for Top 40 category
        // mostly items from another categories doesn't contain Location names
        // i made a map just "to make a map", just for a demo
        // and it's not a valid solution to geocode 100+ items for each request
        if (parentUrl.contains("id=c57943")) {
            updateWithLocation(newCategoryEntities)
        }
    }

    /**
     * Remove items from DB if they are not in the new batch.
     * New data changes frequently, adding or removing some items from response... idk the reason
     * Also it based on IP location and will change if user are moving
     * */
    private suspend fun removeExcessItems(savedItemsIds: List<String>, newCategoryEntitiesIds: List<String>): List<String> {
        val excessItemsIds = mutableListOf<String>()
        val updatedList = savedItemsIds.filter {
            if (it !in newCategoryEntitiesIds) {
                excessItemsIds.add(it)
                return@filter false
            }
            return@filter true
        }
        categoryDao.removeAllByIds(excessItemsIds)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  removed ${excessItemsIds.size} excess entities")

        return updatedList
    }

    /**
     * Free but slow Geocoding api
     * I can't find an extra fast approach to get location but using paid services.
     * Thus app will calculate locations after all data showed to user, to reduce waiting
     * */
    private suspend fun updateWithLocation(newCategoryEntities: List<CategoryEntity>) {
        var entitiesToUpdate = newCategoryEntities
        val hasAudio = entitiesToUpdate.find { it.type == EntityItemType.AUDIO }
        if (hasAudio != null) {
            withContext(Dispatchers.IO) {
                entitiesToUpdate = locationGeocoder.mapToListWithLocations(entitiesToUpdate)
                categoryDao.updateAll(entitiesToUpdate)
                Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  update ${entitiesToUpdate.size} entities with location")
            }
        }
    }

    /**
     * The server is not the best. To save initial values we using base url.
     * */
    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

}