package com.alexeymerov.radiostations.core.data.mapper.category

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.remote.response.CategoryBody

interface CategoryMapper {

    suspend fun mapCategoryResponseToEntity(list: List<CategoryBody>, parentUrl: String): List<CategoryEntity>
}