package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import kotlinx.coroutines.flow.Flow


interface CategoryUseCase {

    fun getCategoriesByUrl(url: String): Flow<CategoryDto>

    suspend fun loadCategoriesByUrl(url: String)

    suspend fun getAudioUrl(url: String): AudioItemDto

}