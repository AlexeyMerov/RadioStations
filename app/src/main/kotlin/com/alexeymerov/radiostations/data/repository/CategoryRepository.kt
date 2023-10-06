package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.remote.response.AudioBody
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategoriesByUrl(url: String): Flow<List<CategoryEntity>>

    suspend fun loadCategoriesByUrl(url: String)

    suspend fun getAudioByUrl(url: String): AudioBody? //todo null is ugly. think about some generic response type (mb kotlin's Result)

}