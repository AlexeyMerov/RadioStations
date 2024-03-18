package com.alexeymerov.radiostations.core.data.repository.audio

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    suspend fun getItemById(id: String): CategoryEntity?

    fun getAllFavorites(): Flow<List<CategoryEntity>>

    suspend fun getItemByUrl(url: String): CategoryEntity?

    suspend fun getMediaByTuneId(tuneId: String): MediaEntity?

    suspend fun changeIsMediaFavorite(itemId: String, isFavorite: Boolean)

    fun getLastPlayingMediaItem(): Flow<MediaEntity?>

    suspend fun setLastPlayingMediaItem(item: MediaEntity)

    suspend fun clearLastPlayingMediaItem()

}