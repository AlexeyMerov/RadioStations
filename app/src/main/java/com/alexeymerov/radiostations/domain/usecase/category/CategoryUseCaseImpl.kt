package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.repository.CategoryRepository
import com.alexeymerov.radiostations.domain.dto.CategoryDto
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.mapper.DtoCategoriesMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
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
        categoryRepository.loadCategoriesByUrl(url)
        return categoryRepository.getCategoriesByUrl(url)
            .map { entityList ->
                var result = mutableListOf<CategoryItemDto>()

                if (entityList.isNotEmpty() && entityList[0].text == ERROR) {
                    return@map CategoryDto(result, isError = true)
                }

                val hasHeaders = entityList.firstOrNull { it.isHeader } != null // if have at least one header then we process all list in a hard way
                Timber.d("new list has headers: $hasHeaders")
                if (hasHeaders) {
                    entityList.forEach { entity ->
                        val stationList = categoryRepository.getStationsByCategory(entity)
                        val dtoList = dtoCategoriesMapper.mapEntitiesToDto(entity, stationList)
                        result.addAll(dtoList)
                    }
                } else {
                    result = dtoCategoriesMapper.mapEntitiesToDto(entityList).toMutableList()
                }

                return@map CategoryDto(result)
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