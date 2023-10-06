package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.common.EMPTY
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

        categories.forEachIndexed { index, entity ->
            val dtoItem = mapCategoryEntityToDto(entity)
            resultList.add(dtoItem)

            if (index != categories.size - 1
                && dtoItem.type == DtoItemType.SUBCATEGORY
                && categories.getOrNull(index + 1)?.type == EntityItemType.SUBCATEGORY
            ) {
                resultList.add(createDivider(dtoItem.url))
            }
        }

        return resultList
    }

    private fun mapCategoryEntityToDto(entity: CategoryEntity): CategoryItemDto {
        val type = when (entity.type) {
            EntityItemType.HEADER -> DtoItemType.HEADER
            EntityItemType.CATEGORY -> DtoItemType.CATEGORY
            EntityItemType.SUBCATEGORY -> DtoItemType.SUBCATEGORY
            EntityItemType.AUDIO -> DtoItemType.AUDIO
        }

        val text = when {
            entity.childCount != null -> "${entity.text} (${entity.childCount})"
            else -> entity.text
        }

        return CategoryItemDto(
            url = entity.url.ifEmpty { text },
            text = text,
            image = entity.image,
            currentTrack = entity.currentTrack,
            type = type
        )
    }

    private fun createDivider(itemUrl: String) = CategoryItemDto(
        url = "$itemUrl#divider",
        text = String.EMPTY,
        image = String.EMPTY,
        currentTrack = String.EMPTY,
        type = DtoItemType.DIVIDER
    )

}