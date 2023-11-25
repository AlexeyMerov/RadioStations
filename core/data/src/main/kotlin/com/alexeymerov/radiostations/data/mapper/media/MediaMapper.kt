package com.alexeymerov.radiostations.data.mapper.media

import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.database.entity.MediaEntity
import com.alexeymerov.radiostations.remote.response.MediaBody

interface MediaMapper {

    fun mapToEntity(categoryEntity: CategoryEntity, body: MediaBody): MediaEntity
}