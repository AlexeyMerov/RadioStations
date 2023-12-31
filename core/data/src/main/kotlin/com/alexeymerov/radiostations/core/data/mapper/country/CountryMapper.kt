package com.alexeymerov.radiostations.core.data.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.response.CountryBody

interface CountryMapper {

    suspend fun mapCountries(list: List<CountryBody>): List<CountryEntity>
}