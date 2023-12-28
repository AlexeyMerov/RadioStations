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
import javax.inject.Inject

class CountryUseCaseImpl @Inject constructor(
    private val countryRepository: CountryRepository,
    private val dtoCountryMapper: DtoCountryMapper
) : CountryUseCase {

    override fun getAllCountries(): Flow<PagingData<CountryDto>> {
        return Pager(
            config = PagingConfig(pageSize = 25),
            pagingSourceFactory = { countryRepository.getCountries() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                dtoCountryMapper.mapEntityToDto(entity)
            }
        }
    }

    override suspend fun loadCountries() {
        countryRepository.loadCountries()
    }
}