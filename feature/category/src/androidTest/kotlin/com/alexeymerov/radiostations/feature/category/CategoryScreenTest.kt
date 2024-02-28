package com.alexeymerov.radiostations.feature.category

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.common.LocalSnackbar
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags
import com.alexeymerov.radiostations.feature.category.item.CategoryScreenTestTags
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class CategoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val validHeader = CategoryItemDto(
        id = "id",
        url = "url1",
        subTitle = "Hello1",
        text = "Station Name Station Name",
        type = DtoItemType.HEADER,
        initials = "HB",
        subItemsCount = 2
    )

    private val validItems = mutableListOf<CategoryItemDto>().apply {
        add(
            CategoryItemDto(
                id = "id98",
                url = "url1",
                subTitle = "Hello1",
                text = "Station Name Station Name",
                type = DtoItemType.SUBCATEGORY,
                initials = "HB",
            )
        )

        add(
            CategoryItemDto(
                id = "id99",
                url = "url1",
                subTitle = "Hello1",
                text = "Station Name Station Name",
                type = DtoItemType.CATEGORY,
                initials = "HB",
            )
        )

        repeat(20) {
            add(
                CategoryItemDto(
                    id = "id$it",
                    url = "url1",
                    subTitle = "Hello1",
                    text = "Station Name Station Name",
                    type = DtoItemType.AUDIO,
                    initials = "HB",
                )
            )
        }
    }

    @Test
    fun whenStateIsLoading_shimmerIsShown() {
        composeTestRule.setContent {
            CategoryScreen(CategoriesViewModel.ViewState.Loading, {}, {}, {})
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.SHIMMER)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsNothingAvailable_errorIsShown() {
        composeTestRule.setContent {
            CategoryScreen(CategoriesViewModel.ViewState.NothingAvailable, {}, {}, {})
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.ERROR_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_itemsIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(null),
                        itemsWithLocation = emptyFlow()
                    ),
                    {}, {}, {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CategoryScreenTestTags.LAZY_LIST)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_headerIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(null),
                        itemsWithLocation = emptyFlow()
                    ),
                    {}, {}, {}
                )
            }
        }


        composeTestRule
            .onAllNodesWithTag(CategoryScreenTestTags.HEADER)
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_categoryIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(null),
                        itemsWithLocation = emptyFlow()
                    ),
                    {}, {}, {}
                )
            }
        }

        composeTestRule
            .onAllNodesWithTag(CategoryScreenTestTags.CATEGORY)
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_stationIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(null),
                        itemsWithLocation = emptyFlow()
                    ),
                    {}, {}, {}
                )
            }
        }

        composeTestRule
            .onAllNodesWithTag(CommonViewTestTags.STATION_LIST_ITEM)
            .onFirst()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithTag(CommonViewTestTags.STAR_ICON)
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun whenListIsScrolled_favScrollToTopIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(null),
                        itemsWithLocation = emptyFlow()
                    ),
                    {}, {}, {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CategoryScreenTestTags.LAZY_LIST)
            .performScrollToIndex(15)

        composeTestRule
            .onNodeWithTag(CategoryScreenTestTags.SCROLL_TOP_FAB)
            .assertIsDisplayed()
    }

    @Test
    fun whenFilterHeadersIsValid_filterChipsAreShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(listOf(validHeader)),
                        itemsWithLocation = emptyFlow()
                    ),
                    {}, {}, {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CategoryScreenTestTags.FILTER_HEADER)
            .assertIsDisplayed()
    }

    @Test
    fun whenItemsWithLocationIsValid_showMapFab() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                CategoryScreen(
                    CategoriesViewModel.ViewState.CategoriesLoaded(
                        categoryItems = flowOf(
                            listOf(
                                HeaderWithItems(validHeader, validItems)
                            )
                        ),
                        filterHeaderItems = MutableStateFlow(listOf(validHeader)),
                        itemsWithLocation = flowOf(
                            LatLngBounds(
                                LatLng(0.0, 0.0),
                                LatLng(1.0, 1.0)
                            ) to validItems
                        )
                    ),
                    {}, {}, {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CategoryScreenTestTags.MAP_FAB)
            .assertIsDisplayed()
    }

}