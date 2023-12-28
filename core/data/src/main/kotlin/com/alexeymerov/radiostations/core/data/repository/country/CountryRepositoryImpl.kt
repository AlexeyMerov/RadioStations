package com.alexeymerov.radiostations.core.data.repository.country

import androidx.paging.PagingSource
import com.alexeymerov.radiostations.core.data.mapper.country.CountryMapper
import com.alexeymerov.radiostations.core.database.dao.CountryDao
import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.client.country.CountryClient
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val countryClient: CountryClient,
    private val countryDao: CountryDao,
    private val countryMapper: CountryMapper,
) : CountryRepository {

    override fun getCountries(): PagingSource<Int, CountryEntity> = countryDao.getAll()

    // it's a static set of data, thus load only if DB empty
    override suspend fun loadCountries() {
        if (countryDao.size() > 0) return
        val response = countryClient.requestAllCountries()
        val mappedCountries = countryMapper.mapCountries(response)
        countryDao.insertAll(mappedCountries)
    }
}