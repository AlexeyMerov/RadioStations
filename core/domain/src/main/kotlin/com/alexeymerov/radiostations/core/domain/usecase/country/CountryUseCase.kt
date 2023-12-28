package com.alexeymerov.radiostations.core.domain.usecase.country

import androidx.paging.PagingData
import com.alexeymerov.radiostations.core.dto.CountryDto
import kotlinx.coroutines.flow.Flow

interface CountryUseCase {

    fun getAllCountries(): Flow<PagingData<CountryDto>>

    suspend fun loadCountries()

}