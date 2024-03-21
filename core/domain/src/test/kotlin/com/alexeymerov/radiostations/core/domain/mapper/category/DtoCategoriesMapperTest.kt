package com.alexeymerov.radiostations.core.domain.mapper.category

import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DtoCategoriesMapperTest {

    private lateinit var mapper: DtoCategoriesMapper

    private val validEntity = CategoryEntity(
        id = "id",
        position = 0,
        url = "url",
        parentUrl = "parentUrl",
        text = "text",
        type = EntityItemType.CATEGORY,
        subTitle = "subTitle",
        latitude = 1.1,
        longitude = 1.2,
        image = "image",
        currentTrack = "currentTrack",
        childCount = 2,
        isFavorite = true
    )

    @Before
    fun setup() {
        mapper = DtoCategoriesMapperImpl()
    }

    @Test
    fun `map valid entity returns valid dto`() {
        val dto = mapper.mapEntityToDto(validEntity)

        assertThat(dto.id).isEqualTo(validEntity.id)
        assertThat(dto.text).isEqualTo(validEntity.text)
        assertThat(dto.subTitle).isEqualTo(validEntity.subTitle)
        assertThat(dto.image).isEqualTo(validEntity.image)
        assertThat(dto.isFavorite).isEqualTo(validEntity.isFavorite)
        assertThat(dto.latitude).isEqualTo(validEntity.latitude)
        assertThat(dto.longitude).isEqualTo(validEntity.longitude)
    }

    @Test
    fun `map HEADER entity returns HEADER dto`() {
        val dto = mapper.mapEntityToDto(validEntity.copy(type = EntityItemType.HEADER))
        assertThat(dto.type).isEqualTo(DtoItemType.HEADER)
    }

    @Test
    fun `map CATEGORY entity returns CATEGORY dto`() {
        val dto = mapper.mapEntityToDto(validEntity.copy(type = EntityItemType.CATEGORY))
        assertThat(dto.type).isEqualTo(DtoItemType.CATEGORY)
    }

    @Test
    fun `map SUBCATEGORY entity returns SUBCATEGORY dto`() {
        val dto = mapper.mapEntityToDto(validEntity.copy(type = EntityItemType.SUBCATEGORY))
        assertThat(dto.type).isEqualTo(DtoItemType.SUBCATEGORY)
    }

    @Test
    fun `map AUDIO entity returns AUDIO dto`() {
        val dto = mapper.mapEntityToDto(validEntity.copy(type = EntityItemType.AUDIO))
        assertThat(dto.type).isEqualTo(DtoItemType.AUDIO)
    }

    @Test
    fun `map audio entity with two words returns valid initials`() {
        val entity = validEntity.copy(type = EntityItemType.AUDIO, text = "Some Text")
        val dto = mapper.mapEntityToDto(entity)
        assertThat(dto.initials).isEqualTo("ST")
    }

    @Test
    fun `map audio entity with single word returns valid initials`() {
        val entity = validEntity.copy(type = EntityItemType.AUDIO, text = "SomeText")
        val dto = mapper.mapEntityToDto(entity)
        assertThat(dto.initials).isEqualTo("S")
    }

    @Test
    fun `map audio entity with many words returns valid initials`() {
        val entity = validEntity.copy(type = EntityItemType.AUDIO, text = "Some Very Long Text")
        val dto = mapper.mapEntityToDto(entity)
        assertThat(dto.initials).isEqualTo("SV")
    }

    @Test
    fun `map entity with child count returns same value`() {
        val entity = validEntity.copy(childCount = 6)
        val dto = mapper.mapEntityToDto(entity)
        assertThat(dto.subItemsCount).isEqualTo(6)
    }

    @Test
    fun `map entity with null child count returns 0`() {
        val entity = validEntity.copy(childCount = null)
        val dto = mapper.mapEntityToDto(entity)
        assertThat(dto.subItemsCount).isEqualTo(0)
    }

    @Test
    fun `map header entity returns text as url`() {
        val entity = validEntity.copy(type = EntityItemType.HEADER, url = "")
        val dto = mapper.mapEntityToDto(entity)
        assertThat(dto.url).isEqualTo(entity.text)
    }

    @Test
    fun `map list entity returns dto list`() {
        val entityList = listOf(validEntity, validEntity, validEntity)
        val dtoList = mapper.mapEntitiesToDto(entityList)

        assertThat(dtoList).hasSize(3)
        assertThat(dtoList[0].text).isEqualTo(dtoList[0].text)
    }

}