package com.alexeymerov.radiostations.data.repository.audio

import com.alexeymerov.radiostations.common.toInt
import com.alexeymerov.radiostations.data.mapper.media.MediaMapper
import com.alexeymerov.radiostations.database.dao.CategoryDao
import com.alexeymerov.radiostations.database.dao.MediaDao
import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.database.entity.MediaEntity
import com.alexeymerov.radiostations.remote.client.radio.RadioClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao, // one day i'll separate db, but not today

    private val mediaMapper: MediaMapper,
    private val mediaDao: MediaDao
) : MediaRepository {

    override fun getAllFavorites(): Flow<List<CategoryEntity>> = categoryDao.getFavorites()

    override suspend fun getMediaByUrl(url: String): MediaEntity? {
        val item = categoryDao.getByUrl(url)
        val mediaBody = radioClient.requestAudioByUrl(url)
        return mediaBody?.let { mediaMapper.mapToEntity(item, it) }
    }

    override suspend fun changeIsMediaFavorite(itemId: String, isFavorite: Boolean) {
        categoryDao.setStationFavorite(itemId, isFavorite.toInt())
    }

    override suspend fun getItemById(id: String): CategoryEntity = categoryDao.getById(id)

    override suspend fun getItemByUrl(url: String): CategoryEntity = categoryDao.getByUrl(url)

    override fun getLastPlayingMediaItem(): Flow<MediaEntity?> = mediaDao.get()

    override suspend fun setLastPlayingMediaItem(item: MediaEntity) = mediaDao.insert(item)
}