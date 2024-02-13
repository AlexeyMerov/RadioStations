package com.alexeymerov.radiostations.core.data.repository.country

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alexeymerov.radiostations.core.database.entity.CountryEntity

class CountryPagingSource(val countries: List<CountryEntity>) : PagingSource<Int, CountryEntity>() {

    override fun getRefreshKey(state: PagingState<Int, CountryEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CountryEntity> {
        return LoadResult.Page(countries, null, null)
    }
}