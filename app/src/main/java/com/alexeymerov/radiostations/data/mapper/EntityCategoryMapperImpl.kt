package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.remote.response.ResponseBody
import javax.inject.Inject

class EntityCategoryMapperImpl @Inject constructor() : EntityCategoryMapper {

    override suspend fun mapCategoryResponseToEntity(list: List<ResponseBody>, parentUrl: String): List<CategoryEntity> {
        return categoryEntities(list, parentUrl)
    }

    private fun categoryEntities(
        list: List<ResponseBody>,
        parentUrl: String,
        startPosition: Int = 0,
        isChildren: Boolean = false
    ): MutableList<CategoryEntity> {
        val result = mutableListOf<CategoryEntity>()
        var index = startPosition
        list.forEach { responseBody ->
            val url = responseBody.url ?: ""
            val children = responseBody.children
            val type = when {
                isChildren && url.contains("Browse.ashx") -> 3 //todo remove and make ENUM or smth
                url.contains("Tune.ashx") -> 2 // audio
                children != null -> 1 // header
                else -> 0 // category
            }
            val item = mapCategoryResponseToEntity(responseBody, parentUrl, index, type)
            result.add(item)
            index++

            if (children != null) {
                val childrenList = categoryEntities(children, parentUrl, index, true)
                result.addAll(childrenList)
                index += childrenList.size
            }
        }

        return result
    }

    private fun mapCategoryResponseToEntity(body: ResponseBody, parentUrl: String, position: Int, type: Int): CategoryEntity {
        return CategoryEntity(
            position = position,
            url = body.url?.httpsEverywhere() ?: "",
            parentUrl = parentUrl,
            text = body.text,
            image = body.image.httpsEverywhere(),
            currentTrack = body.currentTrack,
            type = type
        )
    }

}