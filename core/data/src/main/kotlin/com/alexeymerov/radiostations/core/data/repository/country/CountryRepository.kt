package com.alexeymerov.radiostations.core.data.repository.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

interface CountryRepository {

    fun getCountries(): Flow<List<CountryEntity>>

    suspend fun loadCountries()

}