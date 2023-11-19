package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto

interface DtoCategoriesMapper {

    fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto>

    fun mapEntityToDto(entity: CategoryEntity): CategoryItemDto

    fun extractLocationIfExist(originalText: String): Pair<String, String?>
}