package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.mapper.EntityCategoryMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.data.remote.response.AudioBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
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
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  request new data")
            val parentUrl = url.prepareUrl()
            val categoriesResponse = radioClient.requestCategoriesByUrl(parentUrl)
            val categoryEntities = categoryMapper.mapCategoryResponseToEntity(categoriesResponse, parentUrl)
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]  inserting ${categoryEntities.size} entities")
            categoryDao.insertAll(categoryEntities)
        }
    }

    /**
     * No saving to DB, since not sure it make sense, especially if links inside will be changed.
     * */
    override suspend fun getAudioByUrl(url: String): AudioBody? {
        val audioBodyList = radioClient.requestAudioByUrl(url)
        return if (audioBodyList.isEmpty()) null else audioBodyList[0]
    }

    /**
     * The server is bad boy. To save initial values we using base url.
     * */
    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

}