package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.domain.dto.CategoriesDto

interface DtoCategoriesMapper {

    suspend fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoriesDto>

    suspend fun mapEntitiesToDto(category: CategoryEntity, stationList: List<StationEntity>): List<CategoriesDto>

}