package com.alexeymerov.radiostations.core.data.repository.audio

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeMediaRepository : MediaRepository {

    private var allItems = listOf(
        CategoryEntity(
            id = "id1",
            position = 0,
            url = VALID_ITEM_URL,
            parentUrl = "url",
            text = "SomeText",
            type = EntityItemType.AUDIO,
        ),
        CategoryEntity(
            id = "id2",
            position = 1,
            url = "testurl2",
            parentUrl = "url",
            text = "SomeText",
            type = EntityItemType.AUDIO,
        )
    )

    private val mediaEntity = MediaEntity(
        url = VALID_MEDIA_URL,
        directMediaUrl = "mediaurl",
        imageUrl = "imageUrl",
        title = "title",
        subtitle = "subtitle",
        tuneId = "tuneId"
    )

    private var lastPlaying: MediaEntity? = null

    override suspend fun getItemById(id: String): CategoryEntity? {
        return allItems.find { it.id == id }
    }

    override fun getAllFavorites(): Flow<List<CategoryEntity>> {
        return flowOf(allItems.filter { it.isFavorite })
    }

    override suspend fun getItemByUrl(url: String): CategoryEntity? {
        return allItems.find { it.url == url }
    }

    override suspend fun changeIsMediaFavorite(itemId: String, isFavorite: Boolean) {
        allItems = allItems.map {
            if (it.id == itemId) {
                it.isFavorite = isFavorite
            }
            it
        }
    }

    override suspend fun getMediaByTuneId(tuneId: String): MediaEntity? {
        return if (tuneId == VALID_MEDIA_URL) mediaEntity else null
    }

    override fun getLastPlayingMediaItem(): Flow<MediaEntity?> {
        return flowOf(lastPlaying)
    }

    override suspend fun setLastPlayingMediaItem(item: MediaEntity) {
        lastPlaying = item
    }

    override suspend fun clearLastPlayingMediaItem() {
        lastPlaying = null
    }

    companion object {
        const val VALID_MEDIA_URL = "validurl"
        const val VALID_ITEM_URL = "testurl"
    }
}