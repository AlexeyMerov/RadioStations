package com.alexeymerov.radiostations.core.domain.usecase.country

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alexeymerov.radiostations.core.database.entity.CountryEntity

class CountryPagingSource : PagingSource<Int, CountryEntity>() {

    private val countries = mutableListOf<CountryEntity>()

    var needLoadValidList = false
    var needLoadFilterList = false

    init {
        repeat(10) {
            countries.add(
                CountryEntity(
                    tag = "A" + (it + 65).toChar(),
                    nameEnglish = "United Kingdom",
                    nameNative = "United Kingdom",
                    phoneCode = "$it",
                )
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CountryEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CountryEntity> {
        val resultList = when {
            needLoadFilterList -> listOf(
                CountryEntity(
                    tag = "AA",
                    nameEnglish = "Test Text",
                    nameNative = "United Kingdom",
                    phoneCode = "44",
                )
            )

            needLoadValidList -> countries
            else -> emptyList()
        }

        return LoadResult.Page(resultList, null, null)
    }
}