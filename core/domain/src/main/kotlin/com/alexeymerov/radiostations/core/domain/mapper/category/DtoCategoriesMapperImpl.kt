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

    private val cityRegex = "\\(.+\\)".toRegex()
    private val parenthesisRegex = "\\(|\\)".toRegex()

    override fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto> {
        val resultList = mutableListOf<CategoryItemDto>()

        categories
            .asSequence()
            .map(::mapEntityToDto)
            .forEach(resultList::add)

        return resultList
    }

    override fun mapEntityToDto(entity: CategoryEntity): CategoryItemDto {
        val type = when (entity.type) {
            EntityItemType.HEADER -> DtoItemType.HEADER
            EntityItemType.CATEGORY -> DtoItemType.CATEGORY
            EntityItemType.SUBCATEGORY -> DtoItemType.SUBCATEGORY
            EntityItemType.AUDIO -> DtoItemType.AUDIO
        }

        var mainText = entity.text
        var locationText: String? = null
        var initials = String.EMPTY

        if (type == DtoItemType.AUDIO) {
            val (name, location) = extractLocationIfExist(mainText)
            mainText = name.trim()
            locationText = location

            initials = mainText
                .split(String.SPACE, limit = 2)
                .map { it.first() }
                .joinToString(separator = String.EMPTY)
        }

        return CategoryItemDto(
            id = entity.id,
            type = type,
            url = entity.url.ifEmpty { entity.text },

            text = mainText,
            subText = locationText,
            subItemsCount = entity.childCount ?: 0,

            image = entity.image,
            isFavorite = entity.isFavorite,
            initials = initials
        )
    }

    // if there is (Location), then save it separately and remove from main string.
    override fun extractLocationIfExist(originalText: String): Pair<String, String?> {
        var locationText: String? = null
        val mainText = originalText.replace(cityRegex) { match ->
            locationText = match.value.replace(parenthesisRegex, String.EMPTY).trim()
            return@replace String.EMPTY
        }.trim()

        locationText?.let {
            val uniqueWords = it.split(String.SPACE).toSet()
            locationText = uniqueWords.joinToString(String.SPACE).trim()
        }
        return Pair(mainText, locationText)
    }

}