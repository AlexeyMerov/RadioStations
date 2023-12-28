package com.alexeymerov.radiostations.core.domain.usecase.country

import com.alexeymerov.radiostations.core.dto.CountryDto
import kotlinx.coroutines.flow.Flow

interface CountryUseCase {

    fun getAllCountries(): Flow<List<CountryDto>>

    suspend fun loadCountries()

}