package com.alexeymerov.radiostations.core.data.mapper.geocoder

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity

interface LocationGeocoder {

    suspend fun mapToListWithLocations(list: List<CategoryEntity>): List<CategoryEntity>

}