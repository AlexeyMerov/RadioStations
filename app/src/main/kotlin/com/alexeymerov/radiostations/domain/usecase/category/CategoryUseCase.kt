package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import kotlinx.coroutines.flow.Flow


interface CategoryUseCase {

    fun getCategoriesByUrl(url: String): Flow<CategoryDto>

    fun getFavorites(): Flow<CategoryDto>

    suspend fun loadCategoriesByUrl(url: String)

    suspend fun getAudioUrl(url: String): AudioItemDto

    suspend fun toggleFavorite(item: CategoryItemDto)

    suspend fun toggleFavorite(id: String)

    suspend fun unfavorite(item: CategoryItemDto)

}