package com.alexeymerov.radiostations.core.domain.usecase.country

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

    override fun getAllCountries(): Flow<List<CountryDto>> {
        return countryRepository.getCountries()
            .map(dtoCountryMapper::mapEntitytoDto)
    }

    override suspend fun loadCountries() {
        countryRepository.loadCountries()
    }
}