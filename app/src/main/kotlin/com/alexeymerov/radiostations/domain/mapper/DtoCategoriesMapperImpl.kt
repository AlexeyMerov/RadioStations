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

        if (type == DtoItemType.AUDIO) {
            val (name, location) = extractLocationIfExist(mainText)
            mainText = name
            locationText = location
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

    // if there is (Location), then save it separately and remove from main string.
    override fun extractLocationIfExist(originalText: String): Pair<String, String?> {
        var locationText: String? = null
        val mainText = originalText.replace(cityRegex) { match ->
            locationText = match.value.replace(parenthesisRegex, String.EMPTY).trim()
            return@replace String.EMPTY
        }.trim()

        locationText?.let {
            val uniqueWords = it.split(String.SPACE).toSet()
            locationText = uniqueWords.joinToString(String.SPACE)
        }
        return Pair(mainText, locationText)
    }

}