package com.alexeymerov.radiostations.presentation.common

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.alexeymerov.radiostations.domain.dto.AudioItemDto

fun mapToMediaItem(item: AudioItemDto): MediaItem {
    val mediaMetadata = MediaMetadata.Builder()
        .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
        .setTitle(item.title)
        .setArtist(item.subTitle)
        .setArtworkUri(Uri.parse(item.image))
        .build()

    return MediaItem.Builder()
        .setMediaId(item.directUrl)
        .setUri(item.directUrl)
        .setMediaMetadata(mediaMetadata)
        .build()
}