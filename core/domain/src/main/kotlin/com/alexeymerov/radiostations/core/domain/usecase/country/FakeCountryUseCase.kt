package com.alexeymerov.radiostations.core.domain.usecase.country

import androidx.paging.PagingData
import com.alexeymerov.radiostations.core.dto.CountryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCountryUseCase : CountryUseCase {

    private val countryList = mutableListOf<CountryDto>()

    override fun getAllCountries(searchText: String): Flow<PagingData<CountryDto>> {
        val data = if (searchText.isEmpty()) {
            countryList
        } else {
            countryList
                .filter { it.englishName.contains(searchText, true) }
                .map { it.copy(englishNameHighlights = setOf(IntRange(0, 3))) }
        }

        return flowOf(PagingData.from(data))
    }

    override suspend fun loadCountries() {
        countryList.addAll(
            listOf(
                validCountryDto,
                validCountryDto.copy(
                    tag = "ab",
                    englishName = "England",
                    phoneCode = 5,
                )
            )
        )
    }

    companion object {
        val validCountryDto = CountryDto(
            tag = "aa",
            englishName = "Great",
            nativeName = null,
            phoneCode = 4,
            flagUrl = "",
            englishNameHighlights = null,
            nativeNameHighlights = null,
        )
    }

}