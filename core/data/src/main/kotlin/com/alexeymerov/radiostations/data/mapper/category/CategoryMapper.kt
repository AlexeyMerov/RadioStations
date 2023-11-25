package com.alexeymerov.radiostations.data.mapper.category

import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.remote.response.CategoryBody

interface CategoryMapper {

    suspend fun mapCategoryResponseToEntity(list: List<CategoryBody>, parentUrl: String): List<CategoryEntity>
}