package com.alexeymerov.radiostations.core.domain.mapper.category

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.SPACE
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import javax.inject.Inject

/**
 * @see CategoryItemDto
 * */
class DtoCategoriesMapperImpl @Inject constructor() : DtoCategoriesMapper {

    override fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto> = categories.map(::mapEntityToDto)

    override fun mapEntityToDto(entity: CategoryEntity): CategoryItemDto {
        val type = when (entity.type) {
            EntityItemType.HEADER -> DtoItemType.HEADER
            EntityItemType.CATEGORY -> DtoItemType.CATEGORY
            EntityItemType.SUBCATEGORY -> DtoItemType.SUBCATEGORY
            EntityItemType.AUDIO -> DtoItemType.AUDIO
        }

        var initials = String.EMPTY
        if (type == DtoItemType.AUDIO) {
            initials = entity.text
                .split(String.SPACE, limit = 2)
                .map { it.first() }
                .joinToString(separator = String.EMPTY)
        }

        return CategoryItemDto(
            id = entity.id,
            type = type,

            // text as url is for header
            url = entity.url.ifEmpty { entity.text },

            text = entity.text,
            subTitle = entity.subTitle,
            subItemsCount = entity.childCount ?: 0,

            image = entity.image,
            isFavorite = entity.isFavorite,
            initials = initials,

            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }

}