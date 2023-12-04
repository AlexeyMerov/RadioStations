package com.alexeymerov.radiostations.core.data.mapper.media

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.httpsEverywhere
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.alexeymerov.radiostations.core.remote.response.MediaBody
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