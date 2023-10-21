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

    override suspend fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto> {
        val resultList = mutableListOf<CategoryItemDto>()

        categories
            .asSequence()
            .map(::mapCategoryEntityToDto)
            .forEach(resultList::add)

        return resultList
    }

    private fun mapCategoryEntityToDto(entity: CategoryEntity): CategoryItemDto {
        val type = when (entity.type) {
            EntityItemType.HEADER -> DtoItemType.HEADER
            EntityItemType.CATEGORY -> DtoItemType.CATEGORY
            EntityItemType.SUBCATEGORY -> DtoItemType.SUBCATEGORY
            EntityItemType.AUDIO -> DtoItemType.AUDIO
        }

        return CategoryItemDto(
            url = entity.url.ifEmpty { entity.text },
            text = entity.text,
            image = entity.image,
            type = type,
            subItemsCount = entity.childCount ?: 0
        )
    }

}