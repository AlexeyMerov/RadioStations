package com.alexeymerov.radiostations.data.mapper.media

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.database.entity.MediaEntity
import com.alexeymerov.radiostations.remote.response.MediaBody
import javax.inject.Inject

class MediaMapperImp @Inject constructor() : MediaMapper {

    override fun mapToEntity(categoryEntity: CategoryEntity, body: MediaBody): MediaEntity {
        return MediaEntity(
            url = categoryEntity.url,
            directMediaUrl = body.url.httpsEverywhere(),
            imageUrl = categoryEntity.image,
            title = categoryEntity.text,
            subtitle = String.EMPTY
        )
    }
}