package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import javax.inject.Inject

/**
 * @see CategoryItemDto
 * */
class DtoCategoriesMapperImpl @Inject constructor() : DtoCategoriesMapper {

    override suspend fun mapEntitiesToDto(categories: List<CategoryEntity>) = categories.map { mapCategoryEntityToDto(it, it.type) }

    private fun mapCategoryEntityToDto(entity: CategoryEntity, type: Int) = CategoryItemDto(
        url = entity.url,
        text = entity.text,
        image = entity.image,
        currentTrack = entity.currentTrack,
        type = type
    )

}