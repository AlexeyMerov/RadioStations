package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.common.Cancelable
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity

interface CategoryRepository : Cancelable {

    suspend fun getCategoriesByUrl(url: String): List<CategoryEntity>

    suspend fun loadCategoriesByUrl(url: String)

}