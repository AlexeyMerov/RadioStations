package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto

interface DtoCategoriesMapper {

    suspend fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto>

    suspend fun mapEntitiesToDto(category: CategoryEntity, stationList: List<StationEntity>): List<CategoryItemDto>

}