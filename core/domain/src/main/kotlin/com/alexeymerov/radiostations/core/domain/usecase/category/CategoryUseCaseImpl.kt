package com.alexeymerov.radiostations.core.domain.usecase.category

import com.alexeymerov.radiostations.core.connectivity.ConnectionMonitor
import com.alexeymerov.radiostations.core.data.repository.category.CategoryRepository
import com.alexeymerov.radiostations.core.domain.mapper.DtoCategoriesMapper
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.dto.CategoryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryUseCaseImpl @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val dtoCategoriesMapper: DtoCategoriesMapper,
    private val connectivitySettings: ConnectivitySettingsUseCase,
    private val connectionMonitor: ConnectionMonitor
) : CategoryUseCase {

    /**
     * Make the final list to represent on presentation layer.
     * Adding headers or audio flags.
     * Since we request stations, i prefer not to move it to mapper.
     * */
    override fun getAllByUrl(url: String): Flow<CategoryDto> {
        return categoryRepository.getCategoriesByUrl(url)
            .distinctUntilChanged { old, new -> old == new }
            .map { entityList ->
                if (entityList.isNotEmpty() && entityList[0].text == ERROR) {
                    return@map CategoryDto(emptyList(), isError = true)
                }

                val result = dtoCategoriesMapper.mapEntitiesToDto(entityList)
                return@map CategoryDto(result)
            }
    }

    override suspend fun loadCategoriesByUrl(url: String) {
        if (connectivitySettings.allowConnections() && connectionMonitor.conntectionStatusFlow.value) {
            categoryRepository.loadCategoriesByUrl(url)
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