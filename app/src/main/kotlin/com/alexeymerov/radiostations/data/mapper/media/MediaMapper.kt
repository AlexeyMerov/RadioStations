package com.alexeymerov.radiostations.data.mapper.media

import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.local.db.entity.MediaEntity
import com.alexeymerov.radiostations.remote.response.MediaBody

interface MediaMapper {

    fun mapToEntity(categoryEntity: CategoryEntity, body: MediaBody): MediaEntity
}