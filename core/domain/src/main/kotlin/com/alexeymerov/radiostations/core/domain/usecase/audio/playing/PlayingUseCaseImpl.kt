package com.alexeymerov.radiostations.core.domain.usecase.audio.playing

import com.alexeymerov.radiostations.core.analytics.AnalyticsEvents
import com.alexeymerov.radiostations.core.analytics.AnalyticsParams
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.PlayingUseCase.PlayerState
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class PlayingUseCaseImpl @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val settingsStore: SettingsStore,
    private val analytics: FirebaseAnalytics
) : PlayingUseCase {

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

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        return mediaRepository.getLastPlayingMediaItem()
            .map { media ->
                if (media == null) return@map null

                AudioItemDto(
                    parentUrl = media.url,
                    directUrl = media.directMediaUrl,
                    image = media.imageUrl,
                    imageBase64 = media.imageBase64,
                    title = media.title,
                    subTitle = media.subtitle.ifEmpty { null }
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
            imageBase64 = item.imageBase64,
            title = item.title,
            subtitle = item.subTitle.orEmpty()
        )

        mediaRepository.setLastPlayingMediaItem(mediaEntity)
    }

    private companion object {
        const val PLAYER_STATE_KEY = "player_state"
    }

}