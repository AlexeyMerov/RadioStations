package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto

interface DtoCategoriesMapper {

    suspend fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto>

}