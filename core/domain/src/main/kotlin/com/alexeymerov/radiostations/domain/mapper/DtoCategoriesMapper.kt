package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto

interface DtoCategoriesMapper {

    fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto>

    fun mapEntityToDto(entity: CategoryEntity): CategoryItemDto

    fun extractLocationIfExist(originalText: String): Pair<String, String?>
}