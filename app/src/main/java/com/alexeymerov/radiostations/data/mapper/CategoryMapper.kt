package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.remote.response.ResponseBody
import javax.inject.Inject

class CategoryMapper @Inject constructor() {

    fun mapCategoryResponseToEntity(list: List<ResponseBody>, parentUrl: String): List<CategoryEntity> {
        return list.map { mapCategoryResponseToEntity(it, parentUrl) }
    }

    private fun mapCategoryResponseToEntity(body: ResponseBody, parentUrl: String): CategoryEntity {
        return CategoryEntity(
            url = body.url ?: "",
            parentUrl = parentUrl,
            text = body.text,
            key = body.key ?: ""
        )
    }

}