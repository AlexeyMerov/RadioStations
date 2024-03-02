package com.alexeymerov.radiostations.core.domain.usecase.audio.favorite

import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import kotlinx.coroutines.flow.Flow

interface FavoriteUseCase {

    fun getFavorites(): Flow<CategoryDto>

    suspend fun setFavorite(item: CategoryItemDto)

    suspend fun toggleFavorite(item: CategoryItemDto)

    suspend fun toggleFavorite(id: String)

    suspend fun unfavorite(item: CategoryItemDto)

}