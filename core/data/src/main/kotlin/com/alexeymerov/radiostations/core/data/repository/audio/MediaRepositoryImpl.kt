package com.alexeymerov.radiostations.core.data.repository.audio

import com.alexeymerov.radiostations.core.common.toInt
import com.alexeymerov.radiostations.core.data.mapper.media.MediaMapper
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.database.dao.MediaDao
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val radioClient: RadioClient,
    private val categoryDao: CategoryDao,
    private val mediaMapper: MediaMapper,
    private val mediaDao: MediaDao
) : MediaRepository {

    override fun getAllFavorites(): Flow<List<CategoryEntity>> = categoryDao.getFavoritesFlow()

    override suspend fun getMediaByTuneId(tuneId: String): MediaEntity? {
        val item = categoryDao.getByTuneId(tuneId)
        val mediaBody = radioClient.requestAudioById(tuneId)

        if (item != null && mediaBody != null) {
            return mediaMapper.mapToEntity(item, mediaBody)
        }

        return null
    }

    override suspend fun changeIsMediaFavorite(itemId: String, isFavorite: Boolean) {
        categoryDao.setStationFavorite(itemId, isFavorite.toInt())
    }

    override suspend fun getItemById(id: String): CategoryEntity? = categoryDao.getById(id)

    override suspend fun getItemByUrl(url: String): CategoryEntity? = categoryDao.getByUrl(url)

    override fun getLastPlayingMediaItem(): Flow<MediaEntity?> = mediaDao.getMedia()

    override suspend fun setLastPlayingMediaItem(item: MediaEntity) = mediaDao.insert(item)

    override suspend fun clearLastPlayingMediaItem() = mediaDao.clearTable()
}