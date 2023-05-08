package com.alexeymerov.radiostations.domain.usecase.category

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.repository.CategoryRepository
import com.alexeymerov.radiostations.domain.dto.CategoriesDto
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
     * */
    override fun getCategoriesByUrl(url: String): Flow<List<CategoriesDto>> {
        categoryRepository.loadCategoriesByUrl(url)
        return categoryRepository.getCategoriesByUrl(url)
            .map { entityList ->
                var result = mutableListOf<CategoriesDto>()

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

                return@map result
            }
    }

}