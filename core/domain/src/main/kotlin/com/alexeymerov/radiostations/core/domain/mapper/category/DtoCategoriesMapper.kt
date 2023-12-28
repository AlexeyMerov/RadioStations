package com.alexeymerov.radiostations.core.domain.mapper.category

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.dto.CategoryItemDto

interface DtoCategoriesMapper {

    fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto>

    fun mapEntityToDto(entity: CategoryEntity): CategoryItemDto

    fun extractLocationIfExist(originalText: String): Pair<String, String?>
}