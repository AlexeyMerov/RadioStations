package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.EntityItemType
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import javax.inject.Inject

/**
 * @see CategoryItemDto
 * */
class DtoCategoriesMapperImpl @Inject constructor() : DtoCategoriesMapper {

    override suspend fun mapEntitiesToDto(categories: List<CategoryEntity>) = categories.map { mapCategoryEntityToDto(it) }

    private fun mapCategoryEntityToDto(entity: CategoryEntity): CategoryItemDto {
        val type = when (entity.type) {
            EntityItemType.HEADER -> DtoItemType.HEADER
            EntityItemType.CATEGORY -> DtoItemType.CATEGORY
            EntityItemType.SUBCATEGORY -> DtoItemType.SUBCATEGORY
            EntityItemType.AUDIO -> DtoItemType.AUDIO
        }

        return CategoryItemDto(
            url = entity.url,
            text = entity.text,
            image = entity.image,
            currentTrack = entity.currentTrack,
            type = type
        )
    }

}