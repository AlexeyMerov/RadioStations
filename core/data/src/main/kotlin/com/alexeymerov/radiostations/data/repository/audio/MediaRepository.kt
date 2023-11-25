package com.alexeymerov.radiostations.data.repository.audio

import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    suspend fun getItemById(id: String): CategoryEntity

    fun getAllFavorites(): Flow<List<CategoryEntity>>

    suspend fun getItemByUrl(url: String): CategoryEntity

    suspend fun getMediaByUrl(url: String): MediaEntity?

    suspend fun changeIsMediaFavorite(itemId: String, isFavorite: Boolean)

    fun getLastPlayingMediaItem(): Flow<MediaEntity?>

    suspend fun setLastPlayingMediaItem(item: MediaEntity)

}