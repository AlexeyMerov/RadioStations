package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.remote.response.ResponseBody
import javax.inject.Inject

class CategoryMapper @Inject constructor() {

    suspend fun mapCategoryResponseToEntity(list: List<ResponseBody>): List<CategoryEntity> {
        return list.map { mapCategoryResponseToEntity(it) }
    }

    suspend fun mapCategoryResponseToEntity(body: ResponseBody): CategoryEntity {
        return CategoryEntity(
            name = body.text,
            url = body.url,
            key = body.key
        )
    }

}