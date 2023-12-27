package com.alexeymerov.radiostations.core.data.repository.country

import com.alexeymerov.radiostations.core.data.mapper.country.CountryMapper
import com.alexeymerov.radiostations.core.database.dao.CountryDao
import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.client.country.CountryClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val countryClient: CountryClient,
    private val countryDao: CountryDao,
    private val countryMapper: CountryMapper,
) : CountryRepository {

    override fun getCountries(): Flow<List<CountryEntity>> = countryDao.getAll()

    override suspend fun loadCountries() {
        val response = countryClient.requestAllCountries()
        val mappedCountries = countryMapper.mapCountries(response)
        countryDao.insertAll(mappedCountries)
    }
}