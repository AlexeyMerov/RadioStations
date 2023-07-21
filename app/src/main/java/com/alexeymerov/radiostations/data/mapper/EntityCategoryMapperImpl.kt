package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.EntityItemType
import com.alexeymerov.radiostations.data.remote.response.CategoryBody
import timber.log.Timber
import javax.inject.Inject

class EntityCategoryMapperImpl @Inject constructor() : EntityCategoryMapper {

    override suspend fun mapCategoryResponseToEntity(list: List<CategoryBody>, parentUrl: String): List<CategoryEntity> {
        return categoryEntities(list, parentUrl)
    }

    private fun categoryEntities(
        list: List<CategoryBody>,
        parentUrl: String,
        startPosition: Int = 0,
        isChildren: Boolean = false
    ): MutableList<CategoryEntity> {
        val result = mutableListOf<CategoryEntity>()
        var index = startPosition
        list.forEach { responseBody ->
            val responseChildrenList = responseBody.children
            val responseItemType = responseBody.type.orEmpty()
            val type = when {
                isChildren && responseItemType == TYPE_LINK -> EntityItemType.SUBCATEGORY
                responseItemType == TYPE_AUDIO -> EntityItemType.AUDIO
                responseChildrenList != null -> EntityItemType.HEADER
                else -> EntityItemType.CATEGORY
            }

            val item = mapCategoryResponseToEntity(responseBody, parentUrl, index, type)
            if (item.type == EntityItemType.HEADER) Timber.d("Mymy ${item.text}")
            result.add(item)
            index++

            if (responseChildrenList != null) {
                val childrenEntityList = categoryEntities(responseChildrenList, parentUrl, index, true)
                result.addAll(childrenEntityList)
                index += childrenEntityList.size
            }
        }

        return result
    }

    private fun mapCategoryResponseToEntity(body: CategoryBody, parentUrl: String, position: Int, type: EntityItemType): CategoryEntity {
        return CategoryEntity(
            position = position,
            url = body.url?.httpsEverywhere().orEmpty(),
            parentUrl = parentUrl,
            text = body.text.replace(" \\(\\d+\\)".toRegex(), ""),
            image = body.image.httpsEverywhere(),
            currentTrack = body.currentTrack,
            type = type,
            childCount = body.children?.size
        )
    }

    private companion object {
        const val TYPE_AUDIO = "audio"
        const val TYPE_LINK = "link"
    }

}