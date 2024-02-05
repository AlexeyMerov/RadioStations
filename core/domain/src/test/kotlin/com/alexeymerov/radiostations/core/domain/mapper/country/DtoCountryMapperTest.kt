package com.alexeymerov.radiostations.core.domain.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.google.common.truth.Truth.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DtoCountryMapperTest {

    private lateinit var mapper: DtoCountryMapper

    private val validEntity = CountryEntity(
        tag = "CA",
        nameEnglish = "Canada",
        nameNative = "Native Canada",
        phoneCode = "+1",
    )

    @Before
    fun setup() {
        mapper = DtoCountryMapperImpl()
    }

    @Test
    fun `map valid entity returns valid dto`() {
        val dto = mapper.mapEntityToDto(validEntity)

        assertThat(dto.tag).ignoringCase().isEqualTo(validEntity.tag)
        assertThat(dto.englishName).isEqualTo(validEntity.nameEnglish)
        assertThat(dto.nativeName).isEqualTo(validEntity.nameNative)
    }

    @Test
    fun `map remove + from phoneCode`() {
        val dto = mapper.mapEntityToDto(validEntity)

        assertThat(dto.phoneCode).isEqualTo(1)
    }

    @Test
    fun `map entity with same name and native name returns native as null`() {
        val dto = mapper.mapEntityToDto(validEntity.copy(nameNative = validEntity.nameEnglish))

        assertThat(dto.englishName).isEqualTo(validEntity.nameEnglish)
        assertThat(dto.nativeName).isNull()
    }

    @Test
    fun `map entity return flagCdn svg url for the country`() {
        val dto = mapper.mapEntityToDto(validEntity)

        assertThat(dto.flagUrl).isEqualTo("https://flagcdn.com/ca.svg")
    }

    @Test
    fun `map entity list return dto list with same items`() {
        val dtoList = mapper.mapEntityToDto(listOf(validEntity, validEntity, validEntity))

        assertThat(dtoList).hasSize(3)
        assertThat(dtoList[0].tag).isEqualTo("ca")
    }

    @Test
    fun `search text returns highlights for name`() {
        val dto = mapper.mapEntityToDto(validEntity)
        val dtoWithHighlights = mapper.mapToDtoWithSearchHighlights(dto, "na")

        assertThat(dtoWithHighlights.englishNameHighlights).isNotNull()
        assertThat(dtoWithHighlights.englishNameHighlights).hasSize(1)
        assertThat(dtoWithHighlights.englishNameHighlights!!.first()).isEqualTo(IntRange(2, 4))
    }

    @Test
    fun `search text returns highlights for native name`() {
        val dto = mapper.mapEntityToDto(validEntity)
        val dtoWithHighlights = mapper.mapToDtoWithSearchHighlights(dto, "na")

        assertThat(dtoWithHighlights.nativeNameHighlights).isNotNull()
        assertThat(dtoWithHighlights.nativeNameHighlights).hasSize(2)
        assertThat(dtoWithHighlights.nativeNameHighlights!!.first()).isEqualTo(IntRange(0, 2))
        assertThat(dtoWithHighlights.nativeNameHighlights!!.last()).isEqualTo(IntRange(9, 11))
    }

    @Test
    fun `search invalid text returns empty set`() {
        val dto = mapper.mapEntityToDto(validEntity)
        val dtoWithHighlights = mapper.mapToDtoWithSearchHighlights(dto, "nn")

        assertThat(dtoWithHighlights.englishNameHighlights).isEmpty()
    }
}