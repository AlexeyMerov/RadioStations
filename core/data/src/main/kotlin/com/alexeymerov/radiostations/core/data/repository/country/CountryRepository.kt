package com.alexeymerov.radiostations.core.data.repository.country

import androidx.paging.PagingSource
import com.alexeymerov.radiostations.core.database.entity.CountryEntity

interface CountryRepository {

    fun getCountries(): PagingSource<Int, CountryEntity>

    fun getCountriesByText(searchText: String): PagingSource<Int, CountryEntity>

    suspend fun loadCountries()

}