package com.alexeymerov.radiostations.core.domain.usecase.country

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.alexeymerov.radiostations.core.data.repository.country.CountryRepository
import com.alexeymerov.radiostations.core.domain.mapper.country.DtoCountryMapper
import com.alexeymerov.radiostations.core.dto.CountryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CountryUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository,
    private val dtoCountryMapper: DtoCountryMapper
) : CountryUseCase {

    override fun getAllCountries(searchText: String): Flow<PagingData<CountryDto>> {
        val pager = Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = {
                if (searchText.isEmpty()) {
                    countryRepository.getCountries()
                } else {
                    countryRepository.getCountriesByText(searchText)
                }
            }
        )

        return pager.flow.map { pagingData ->
            pagingData.map { entity ->
                var result = dtoCountryMapper.mapEntityToDto(entity)

                if (searchText.isNotEmpty()) {
                    Timber.d("getAllCountries map Highlights")
                    result = dtoCountryMapper.mapToDtoWithSearchHighlights(result, searchText)
                }

                result
            }
        }
    }

    override suspend fun loadCountries() {
        countryRepository.loadCountries()
    }

    private companion object {
        const val ITEMS_PER_PAGE = 25
    }
}