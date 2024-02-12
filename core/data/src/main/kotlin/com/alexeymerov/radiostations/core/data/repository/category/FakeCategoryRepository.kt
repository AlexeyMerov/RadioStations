package com.alexeymerov.radiostations.core.data.repository.category

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCategoryRepository : CategoryRepository {

    private val entityList = mutableListOf<CategoryEntity>()

    override fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>> {
        return if (url == VALID_URL || url == ERROR_URL) flowOf(entityList) else flowOf(emptyList())
    }

    override suspend fun loadCategoriesByUrl(url: String) {
        val newData = mutableListOf<CategoryEntity>()

        if (url == VALID_URL) {
            repeat(10) {
                val entity = CategoryEntity(
                    id = "id$it",
                    position = it,
                    url = "",
                    parentUrl = "",
                    text = "",
                    type = EntityItemType.CATEGORY
                )
                newData.add(entity)
            }
        } else if (url == ERROR_URL) {
            newData.add(
                CategoryEntity(
                    id = "id",
                    position = 0,
                    url = "",
                    parentUrl = "",
                    text = "No stations or shows available",
                    type = EntityItemType.CATEGORY
                )
            )
        }

        entityList.addAll(newData)
    }

    companion object {
        const val VALID_URL = "validurl"
        const val ERROR_URL = "errorurl"
    }
}