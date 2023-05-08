package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.domain.dto.CategoriesDto
import javax.inject.Inject

class DtoCategoriesMapperImpl @Inject constructor() : DtoCategoriesMapper {

    override suspend fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoriesDto> {
        return categories.map { mapCategoryEntityToDto(it) }
    }

    override suspend fun mapEntitiesToDto(category: CategoryEntity, stationList: List<StationEntity>): List<CategoriesDto> {
        val result = mutableListOf<CategoriesDto>()

        val header = mapCategoryEntityToDto(category)
        result.add(header)

        val stations = stationList.map { mapStationEntityToDto(it) }
        result.addAll(stations)

        return result
    }

    private fun mapCategoryEntityToDto(entity: CategoryEntity) = CategoriesDto(
        isHeader = entity.isHeader,
        url = entity.url,
        text = entity.text,
        image = null,
        currentTrack = null
    )

    private fun mapStationEntityToDto(entity: StationEntity) = CategoriesDto(
        url = entity.url,
        text = entity.text,
        image = entity.image,
        currentTrack = entity.currentTrack,
        isAudio = true
    )
}