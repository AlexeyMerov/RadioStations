package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.common.Cancelable
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository : Cancelable {

    fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>>

    fun loadCategoriesByUrl(url: String)

}