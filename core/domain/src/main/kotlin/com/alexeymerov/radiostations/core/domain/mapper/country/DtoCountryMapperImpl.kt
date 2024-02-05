package com.alexeymerov.radiostations.core.domain.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.dto.CountryDto
import javax.inject.Inject

class DtoCountryMapperImpl @Inject constructor() : DtoCountryMapper {

    override fun mapEntityToDto(list: List<CountryEntity>): List<CountryDto> = list.map(::mapEntityToDto)

    override fun mapEntityToDto(entity: CountryEntity): CountryDto {
        val tag = entity.tag.lowercase()
        val nativeName = if (entity.nameNative == entity.nameEnglish) null else entity.nameNative
        val phoneCode = entity.phoneCode.replace("+", "").toInt()

        return CountryDto(
            tag = tag,
            englishName = entity.nameEnglish,
            nativeName = nativeName,
            phoneCode = phoneCode,
            flagUrl = FLAG_BASE_URL + tag + FLAG_EXT
        )
    }

    override fun mapToDtoWithSearchHighlights(dto: CountryDto, searchText: String): CountryDto {
        return dto.copy(
            englishNameHighlights = findIntRanges(dto.englishName, searchText),
            nativeNameHighlights = dto.nativeName?.let { findIntRanges(it, searchText) }
        )
    }

    private fun findIntRanges(originalText: String, searchText: String): Set<IntRange> {
        return searchText.lowercase()
            .toRegex()
            .findAll(originalText.lowercase())
            .map {
                IntRange(
                    start = it.range.first,
                    endInclusive = it.range.last + 1
                )
            }
            .toSet()
    }

    private companion object {
        const val FLAG_BASE_URL = "https://flagcdn.com/"
        const val FLAG_EXT = ".svg"
    }
}