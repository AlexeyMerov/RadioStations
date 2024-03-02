package com.alexeymerov.radiostations.core.domain.usecase.audio.favorite

import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapper
import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class FavoriteUseCaseImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dtoCategoriesMapper: DtoCategoriesMapper,
    private val analytics: FirebaseAnalytics
) : FavoriteUseCase {

    override fun getFavorites(): Flow<CategoryDto> {
        return mediaRepository.getAllFavorites()
            .distinctUntilChanged { old, new -> old == new }
            .map { entityList ->
                if (entityList.isNotEmpty() && entityList[0].text == ERROR) {
                    return@map CategoryDto(emptyList(), isError = true)
                }

                val result = dtoCategoriesMapper.mapEntitiesToDto(entityList)
                return@map CategoryDto(result)
            }
    }

    override suspend fun setFavorite(item: CategoryItemDto) {
        changeIsMediaFavorite(item.id, item.text, true)
    }

    override suspend fun toggleFavorite(item: CategoryItemDto) {
        changeIsMediaFavorite(item.id, item.text, !item.isFavorite)
    }

    override suspend fun toggleFavorite(id: String) {
        val item = mediaRepository.getItemById(id)
        if (item == null) {
            Timber.w("toggleFavorite cant find an item")
            return
        }
        changeIsMediaFavorite(id, item.text, !item.isFavorite)
    }

    override suspend fun unfavorite(item: CategoryItemDto) {
        changeIsMediaFavorite(item.id, item.text, false)
    }

    private suspend fun changeIsMediaFavorite(id: String, title: String, isFavorite: Boolean) {
        val eventName = if (isFavorite) "favorite_media" else "unfavorite_media"
        analytics.logEvent(eventName) {
            param(AnalyticsParams.TITLE, title)
        }
        mediaRepository.changeIsMediaFavorite(id, isFavorite)
    }

    private companion object {
        /**
         * At the moment server not returns any normal types to recognize an error,
         * so we just checking with hardcoded strings
         * */
        const val ERROR = "No stations or shows available"
    }
}