package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.remote.response.CategoryBody

interface EntityCategoryMapper {

    suspend fun mapCategoryResponseToEntity(list: List<CategoryBody>, parentUrl: String): List<CategoryEntity>
}