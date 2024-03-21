package com.alexeymerov.radiostations.core.domain.usecase.category

import com.alexeymerov.radiostations.core.dto.CategoryDto
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCategoryUseCase : CategoryUseCase {

    var delay = 0L

    var emulateError = false

    var returnEmptyList = false

    var addHeaders = false

    var addLocations = false

    private val state = MutableStateFlow(emptyList<CategoryItemDto>())

    override fun getAllByUrl(url: String): Flow<CategoryDto> {
        return state
            .map {
                delay(delay)
                CategoryDto(
                    items = if (returnEmptyList) emptyList() else it,
                    isError = emulateError
                )
            }
    }

    override suspend fun getByTuneId(tuneId: String): CategoryItemDto? {
        val validData = CategoryItemDto(
            id = "id",
            url = "url",
            subTitle = "Hello",
            text = "Station Name",
            type = DtoItemType.AUDIO,
            initials = "HA",
        )
        return when (tuneId) {
            VALID_ID -> validData
            VALID_ID_IS_FAVORITE -> validData.copy(isFavorite = true)
            VALID_ID_NO_SUBTITLE -> validData.copy(subTitle = null)
            else -> null
        }
    }

    override suspend fun loadCategoriesByUrl(url: String) {
        if (url == VALID_URL) {
            val result = mutableListOf<CategoryItemDto>()

            if (state.value.isNotEmpty()) {
                result.addAll(state.value)
                result.add(
                    CategoryItemDto(
                        id = "id9",
                        url = "url",
                        subTitle = "Hello",
                        text = "Station Name",
                        type = DtoItemType.AUDIO,
                        initials = "HA"
                    )
                )
            } else {
                if (addHeaders) {
                    result.add(
                        CategoryItemDto(
                            id = "id1",
                            url = "url1",
                            subTitle = "Hello1",
                            text = "Station Name Station Name",
                            type = DtoItemType.HEADER,
                            initials = "HB",
                            subItemsCount = 2
                        )
                    )
                }

                result.add(
                    CategoryItemDto(
                        id = "id",
                        url = "url",
                        subTitle = "Hello",
                        text = "Station Name",
                        type = DtoItemType.AUDIO,
                        initials = "HA"
                    ).apply {
                        if (addLocations) {
                            latitude = 13.23455
                            longitude = 1.3495
                        }
                    }
                )

                if (addHeaders) {
                    result.add(
                        CategoryItemDto(
                            id = "id2",
                            url = "url1",
                            subTitle = "Hello1",
                            text = "Station Name Station Name",
                            type = DtoItemType.HEADER,
                            initials = "HB",
                            subItemsCount = 2
                        )
                    )
                }

                result.add(
                    CategoryItemDto(
                        id = "id1",
                        url = "url1",
                        subTitle = "Hello1",
                        text = "Station Name Station Name",
                        type = DtoItemType.AUDIO,
                        initials = "HB"
                    )
                )
            }
            state.value = result
        }
    }

    companion object {
        const val VALID_URL = "validurl"

        const val VALID_ID = "VALID_ID"
        const val VALID_ID_NO_SUBTITLE = "VALID_ID_NO_SUBTITLE"
        const val VALID_ID_IS_FAVORITE = "VALID_ID_IS_FAVORITE"
    }
}