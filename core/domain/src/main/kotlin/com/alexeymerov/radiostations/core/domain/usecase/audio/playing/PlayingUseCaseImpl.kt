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

    // one ugly solution please...
    private var shouldPlay: Boolean? = null

    override fun getPlayerState(): Flow<PlayerState> {
        Timber.d("-> getPlayerState: ")
        return settingsStore.getIntPrefsFlow(PLAYER_STATE_KEY, defValue = PlayerState.Empty.value)
            .map { prefValue ->
                Timber.d("-> getPlayerState: prefValue $prefValue")
                when (prefValue) {
                    PlayerState.Playing().value -> {
                        shouldPlay = true
                        PlayerState.Playing()
                    }

                    PlayerState.Stopped().value -> PlayerState.Stopped()
                    PlayerState.Loading.value -> PlayerState.Loading
                    else -> PlayerState.Empty
                }
            }
    }

    override suspend fun updatePlayerState(newState: PlayerState) {
        Timber.d("updatePlayerState ${getPlayerState().first()} --> $newState ## shouldPlay = $shouldPlay")

        var resultState = newState

        if (newState.isUserAction) {
            if (newState is PlayerState.Playing) {
                shouldPlay = true
            } else if (newState is PlayerState.Stopped) {
                shouldPlay = false
            }
        }

        val currentState = getPlayerState().first()

        val isTheSameState = newState == currentState
        val ifStopAfterEmpty = newState is PlayerState.Stopped && currentState == PlayerState.Empty
        if (isTheSameState || ifStopAfterEmpty) return

        val ifPlayButNotFromUser = newState is PlayerState.Playing
            && currentState is PlayerState.Loading
            && shouldPlay == null || shouldPlay == false

        if (ifPlayButNotFromUser) resultState = PlayerState.Stopped()

        Timber.d("updatePlayerState updating...")
        if (resultState is PlayerState.Empty) mediaRepository.clearLastPlayingMediaItem()
        settingsStore.setIntPrefs(PLAYER_STATE_KEY, resultState.value)
    }

    override suspend fun togglePlayerPlayStop() {
        Timber.d("-> togglePlayerPlayStop: ")
        val newState = when (getPlayerState().first()) {
            is PlayerState.Playing -> PlayerState.Stopped(true)
            else -> PlayerState.Playing(true)
        }
        updatePlayerState(newState)
    }

    override fun getLastPlayingMediaItem(): Flow<AudioItemDto?> {
        Timber.d("-> getLastPlayingMediaItem: ")
        return mediaRepository.getLastPlayingMediaItem()
            .map { media ->
                if (media == null) return@map null

                AudioItemDto(
                    parentUrl = media.url,
                    directUrl = media.directMediaUrl,
                    image = media.imageUrl,
                    imageBase64 = media.imageBase64,
                    title = media.title,
                    subTitle = media.subtitle.ifEmpty { null },
                    tuneId = media.tuneId.orEmpty()
                )
            }
    }

    override suspend fun setLastPlayingMedia(item: AudioItemDto) {
        Timber.d("-> setLastPlayingMedia: ")
        analytics.logEvent(AnalyticsEvents.PLAY_MEDIA) {
            param(AnalyticsParams.TITLE, item.title)
        }
        val mediaEntity = MediaEntity(
            url = item.parentUrl,
            directMediaUrl = item.directUrl,
            imageUrl = item.image,
            imageBase64 = item.imageBase64,
            title = item.title,
            subtitle = item.subTitle.orEmpty(),
            tuneId = item.tuneId
        )

        shouldPlay = true

        mediaRepository.setLastPlayingMediaItem(mediaEntity)
    }

    private companion object {
        const val PLAYER_STATE_KEY = "player_state"
    }

}