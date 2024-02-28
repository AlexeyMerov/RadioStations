package com.alexeymerov.radiostations.core.domain.usecase.audio

import com.alexeymerov.radiostations.core.analytics.AnalyticsEvents
import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.extractTextFromRoundBrackets
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapper
import com.alexeymerov.radiostations.core.domain.usecase.audio.AudioUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class AudioUseCaseImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dtoCategoriesMapper: DtoCategoriesMapper,
    private val settingsStore: SettingsStore,
    private val analytics: FirebaseAnalytics
) : AudioUseCase {

    override suspend fun getByUrl(url: String): CategoryItemDto? {
        val entity = mediaRepository.getItemByUrl(url) ?: return null
        return dtoCategoriesMapper.mapEntityToDto(entity)
    }

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

    override suspend fun getMediaItem(url: String): AudioItemDto? {
        val mediaEntity = mediaRepository.getMediaByUrl(url)
        return when {
            mediaEntity != null -> {
                val (title, subtitle) = mediaEntity.title.extractTextFromRoundBrackets()
                AudioItemDto(
                    parentUrl = mediaEntity.url,
                    directUrl = mediaEntity.directMediaUrl,
                    image = mediaEntity.imageUrl,
                    title = title,
                    subTitle = subtitle ?: String.EMPTY
                )
            }

            else -> null
        }
    }

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        return mediaRepository.getLastPlayingMediaItem()
            .map {
                if (it == null) return@map null

                AudioItemDto(
                    parentUrl = it.url,
                    directUrl = it.directMediaUrl,
                    image = it.imageUrl,
                    title = it.title,
                    subTitle = it.subtitle
                )
            }
    }

    override suspend fun setLastPlayingMedia(item: AudioItemDto) {
        analytics.logEvent(AnalyticsEvents.PLAY_MEDIA) {
            param(AnalyticsParams.TITLE, item.title)
        }
        val mediaEntity = MediaEntity(
            url = item.parentUrl,
            directMediaUrl = item.directUrl,
            imageUrl = item.image,
            title = item.title,
            subtitle = item.subTitle
        )

        mediaRepository.setLastPlayingMediaItem(mediaEntity)
    }

    override fun getPlayerState(): Flow<PlayerState> {
        return settingsStore.getIntPrefsFlow(PLAYER_STATE_KEY, defValue = PlayerState.EMPTY.value)
            .map { prefValue -> PlayerState.entries.first { it.value == prefValue } }
    }

    override suspend fun updatePlayerState(newState: PlayerState) {
        Timber.d("updatePlayerState $newState")

        val currentState = getPlayerState().first()
        if (newState == currentState) return
        if (newState == PlayerState.STOPPED && currentState == PlayerState.EMPTY) return

        settingsStore.setIntPrefs(PLAYER_STATE_KEY, newState.value)
    }

    override suspend fun togglePlayerPlayStop() {
        val newState = when (getPlayerState().first()) {
            PlayerState.PLAYING -> PlayerState.STOPPED
            else -> PlayerState.PLAYING
        }
        updatePlayerState(newState)
    }

    companion object {
        /**
         * At the moment server not returns any normal types to recognize an error,
         * so we just checking with hardcoded strings
         * */
        private const val ERROR = "No stations or shows available"

        private const val PLAYER_STATE_KEY = "player_state"
    }
}