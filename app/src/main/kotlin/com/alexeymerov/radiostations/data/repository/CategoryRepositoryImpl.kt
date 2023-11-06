package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.BuildConfig
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.common.toInt
import com.alexeymerov.radiostations.data.local.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.mapper.EntityCategoryMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.data.remote.response.AudioBody
import com.alexeymerov.radiostations.data.remote.response.MainBody
import com.alexeymerov.radiostations.data.remote.response.ServerBodyType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
    private val categoryMapper: EntityCategoryMapper
) : CategoryRepository {

    override suspend fun getItemById(id: String): CategoryEntity {
        return categoryDao.getById(id)
    }

    override fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>> {
        return categoryDao.getAllByParentUrl(url.prepareUrl())
    }

    override fun getFavorites(): Flow<List<CategoryEntity>> {
        return categoryDao.getFavorites()
    }

    /**
     * The server is bad boy. So we use URL as only reliable parameter to operate with.
     *
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
     * No saving to DB, since not sure it make sense, especially if links inside will be changed.
     * */
    override suspend fun getAudioByUrl(url: String): AudioBody? {
        val audioBodyResponse = radioClient.requestAudioByUrl(url)
        val audioBodyList = mapResponseBody(audioBodyResponse)
        return if (audioBodyList.isEmpty()) null else audioBodyList[0]
    }

    override suspend fun changeStationFavorite(itemId: String, isFavorite: Boolean) {
        categoryDao.setStationFavorite(itemId, isFavorite.toInt())
    }

    /**
     * The server is bad boy. To save initial values we using base url.
     * */
    private fun String.prepareUrl() = ifEmpty { BuildConfig.BASE_URL }.httpsEverywhere()

    //no error handling at the moment since uncertainty of server errors and response format
    private fun <T : ServerBodyType> mapResponseBody(body: Response<MainBody<T>>): List<T> {
        var errorText: String? = null
        var resultList = emptyList<T>()
        val mainBody = body.body()
        when {
            !body.isSuccessful -> errorText = body.message()
            mainBody == null -> errorText = "Response body is null"
            mainBody.head.status != STATUS_OK -> errorText = mainBody.head.title ?: "Response status: ${mainBody.head.status}"
            else -> resultList = mainBody.body
        }

        if (errorText != null) Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] $errorText")

        return resultList
    }

    private companion object {
        const val STATUS_OK = "200"
    }

}