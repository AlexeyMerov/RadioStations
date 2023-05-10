package com.alexeymerov.radiostations.domain.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import javax.inject.Inject

/**
 * @see CategoryItemDto
 * */
class DtoCategoriesMapperImpl @Inject constructor() : DtoCategoriesMapper {

    override suspend fun mapEntitiesToDto(categories: List<CategoryEntity>): List<CategoryItemDto> {
        return categories.map { mapCategoryEntityToDto(it) }
    }

    override suspend fun mapEntitiesToDto(category: CategoryEntity, stationList: List<StationEntity>): List<CategoryItemDto> {
        val result = mutableListOf<CategoryItemDto>()

        val header = mapCategoryEntityToDto(category)
        result.add(header)

        val stations = stationList.map { mapStationEntityToDto(it) }
        result.addAll(stations)

        return result
    }

    private fun mapCategoryEntityToDto(entity: CategoryEntity) = CategoryItemDto(
        isHeader = entity.isHeader,
        url = entity.url,
        text = entity.text,
        image = null,
        currentTrack = null
    )

    private fun mapStationEntityToDto(entity: StationEntity) = CategoryItemDto(
        url = entity.url,
        text = entity.text,
        image = entity.image,
        currentTrack = entity.currentTrack,
        isAudio = true
    )
}