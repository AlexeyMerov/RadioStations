package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.repository.CategoryRepository
import com.alexeymerov.radiostations.domain.dto.AudioItemDto
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryUseCaseImpl @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val dtoCategoriesMapper: DtoCategoriesMapper
) : CategoryUseCase, BaseCoroutineScope() {

    /**
     * Make the final list to represent on presentation layer.
     * Adding headers or audio flags.
     * Since we request stations, i prefer not to move it to mapper.
     * */
    override fun getCategoriesByUrl(url: String): Flow<CategoryDto> {
        return categoryRepository.getCategoriesByUrl(url)
            .map { entityList ->
                if (entityList.isNotEmpty() && entityList[0].text == ERROR) {
                    return@map CategoryDto(emptyList(), isError = true)
                }

                val result = dtoCategoriesMapper.mapEntitiesToDto(entityList)
                return@map CategoryDto(result)
            }
    }

    override fun loadCategoriesByUrl(url: String) = categoryRepository.loadCategoriesByUrl(url)

    override suspend fun getAudioUrl(url: String): AudioItemDto {
        val audioUrl = categoryRepository.getAudioByUrl(url)?.url
        return when (audioUrl) {
            null -> AudioItemDto(isError = true)
            else -> AudioItemDto(url = audioUrl.httpsEverywhere())
        }
    }

    private companion object {
        /**
         * At the moment server not returns any normal types to recognize an error,
         * so we just checking with hardcoded strings
         * */
        private const val ERROR = "No stations or shows available"
    }

}