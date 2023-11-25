package com.alexeymerov.radiostations.data.mapper.category

import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.remote.response.CategoryBody

interface CategoryMapper {

    suspend fun mapCategoryResponseToEntity(list: List<CategoryBody>, parentUrl: String): List<CategoryEntity>
}