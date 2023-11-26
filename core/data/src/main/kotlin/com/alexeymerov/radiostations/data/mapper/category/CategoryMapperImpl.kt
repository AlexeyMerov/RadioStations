package com.alexeymerov.radiostations.data.mapper.category

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.database.entity.CategoryEntity
import com.alexeymerov.radiostations.database.entity.EntityItemType
import com.alexeymerov.radiostations.remote.client.NetworkDefaults
import com.alexeymerov.radiostations.remote.response.CategoryBody
import timber.log.Timber
import javax.inject.Inject

class CategoryMapperImpl @Inject constructor() : CategoryMapper {

    private val invalidChildCountStringRegex = " \\(\\d+\\)".toRegex()

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
            if (parametersAreInvalid(parentUrl, responseBody, isChildren)) return@forEach

            val responseChildrenList = responseBody.children
            val responseItemType = responseBody.type.orEmpty()
            val type = when {
                isChildren && responseItemType == NetworkDefaults.TYPE_LINK -> EntityItemType.SUBCATEGORY
                responseItemType == NetworkDefaults.TYPE_AUDIO -> EntityItemType.AUDIO
                !responseBody.children.isNullOrEmpty() -> EntityItemType.HEADER
                else -> EntityItemType.CATEGORY
            }

            val item = mapCategoryResponseToEntity(responseBody, parentUrl, index, type)
            if (item.type == EntityItemType.HEADER) Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${item.text}")
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

    private fun parametersAreInvalid(parentUrl: String, responseBody: CategoryBody, isChildren: Boolean): Boolean {
        // text and link should be valid
        if (!parentUrl.matches(NetworkDefaults.REGEX_VALID_URL) || responseBody.text.isEmpty()) return true

        val responseUrl = responseBody.url

        // if header but without children
        if (responseUrl.isNullOrEmpty() && responseBody.children.isNullOrEmpty()) return true

        // if subcategory/audio with broken link
        if (isChildren && (responseUrl == null || !responseUrl.matches(NetworkDefaults.REGEX_VALID_URL))) return true

        return false
    }

    private fun mapCategoryResponseToEntity(body: CategoryBody, parentUrl: String, position: Int, type: EntityItemType): CategoryEntity {
        val text = body.text.replace(invalidChildCountStringRegex, String.EMPTY)
        return CategoryEntity(
            id = "$parentUrl##$text",
            position = position,
            url = body.url?.httpsEverywhere().orEmpty(),
            parentUrl = parentUrl,
            text = text,
            image = body.image.httpsEverywhere(),
            currentTrack = body.currentTrack,
            type = type,
            childCount = body.children?.size
        )
    }
}