package com.alexeymerov.radiostations.core.data.mapper.media

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.alexeymerov.radiostations.core.remote.response.MediaBody

interface MediaMapper {

    fun mapToEntity(categoryEntity: CategoryEntity, body: MediaBody): MediaEntity
}