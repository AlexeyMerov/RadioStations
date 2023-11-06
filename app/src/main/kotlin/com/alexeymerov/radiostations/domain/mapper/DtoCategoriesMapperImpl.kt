package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.common.SPACE
import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.local.db.entity.EntityItemType
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import javax.inject.Inject

/**
 * @see CategoryItemDto
 * */
class DtoCategoriesMapperImpl @Inject constructor() : DtoCategoriesMapper {

    private val cityRegex = "\\(.+\\)".toRegex()
    private val parenthesisRegex = "\\(|\\)".toRegex()

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

        var mainText = entity.text
        var locationText: String? = null

        if (type == DtoItemType.AUDIO) {
            mainText = entity.text.replace(cityRegex) { match ->
                locationText = match.value.replace(parenthesisRegex, String.EMPTY).trim()
                return@replace String.EMPTY
            }.trim()

            locationText?.let {
                val uniqueWords = it.split(String.SPACE).toSet()
                locationText = uniqueWords.joinToString(String.SPACE)
            }
        }

        val initials = mainText
            .split(String.SPACE, limit = 2)
            .map { it.first() }
            .joinToString(separator = String.EMPTY)

        return CategoryItemDto(
            id = entity.id,
            url = entity.url.ifEmpty { entity.text },
            text = mainText,
            subText = locationText,
            image = entity.image,
            type = type,
            subItemsCount = entity.childCount ?: 0,
            isFavorite = entity.isFavorite,
            initials = initials
        )
    }

}