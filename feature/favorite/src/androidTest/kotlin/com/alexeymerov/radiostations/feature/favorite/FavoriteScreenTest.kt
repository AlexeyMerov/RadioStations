package com.alexeymerov.radiostations.feature.favorite

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.ViewType
import com.alexeymerov.radiostations.core.dto.CategoryItemDto
import com.alexeymerov.radiostations.core.dto.DtoItemType
import com.alexeymerov.radiostations.core.ui.common.LocalSnackbar
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags.LIST_ITEM
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags.SELECTED_ICON
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags.STAR_ICON
import com.alexeymerov.radiostations.feature.favorite.FavoriteTestTags.GRID_ITEM
import com.alexeymerov.radiostations.feature.favorite.FavoriteTestTags.LAZY_LIST_GRID
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.ViewState
import org.junit.Rule
import org.junit.Test

class FavoriteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val validItems = listOf(
        CategoryItemDto(
            id = "id",
            url = "url",
            subTitle = "Hello",
            text = "Station Name",
            type = DtoItemType.AUDIO,
            isFavorite = true,
            initials = "HA"
        ),
        CategoryItemDto(
            id = "id1",
            url = "url1",
            subTitle = "Hello1",
            text = "Station Name Station Name",
            type = DtoItemType.AUDIO,
            isFavorite = true,
            initials = "HB"
        )
    )

    @Test
    fun whenStateIsLoading_loaderIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.Loading,
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.LOADER)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsNothingAvailable_errorIsShown() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.NothingAvailable,
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.ERROR_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateLoaded_withListType_showListItems() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.LIST),
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(LAZY_LIST_GRID)
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithTag(LIST_ITEM)
            .assertAllIsDisplayed()
    }

    @Test
    fun whenStateLoaded_withGrid2Type_showGridItems() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.GRID_2_COLUMN),
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(LAZY_LIST_GRID)
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithTag(GRID_ITEM)
            .assertAllIsDisplayed()
    }

    @Test
    fun whenStateLoaded_withGrid3Type_showGridItems() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.GRID_3_COLUMN),
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag(LAZY_LIST_GRID)
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithTag(GRID_ITEM)
            .assertAllIsDisplayed()
    }

    @Test
    fun whenList_InSelection_favIconHidden() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.GRID_3_COLUMN),
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onAllNodesWithTag(GRID_ITEM)
            .assertAllIsDisplayed()
    }

    @Test
    fun whenGrid_InSelection_favIconHidden() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.GRID_3_COLUMN),
                    viewEffect = null,
                    inSelection = true,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onAllNodesWithTag(STAR_ICON)
            .assertAllIsNotDisplayed()
    }

    @Test
    fun whenList_onLongClick_showSelectedIcon() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.LIST),
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onAllNodesWithTag(LIST_ITEM)
            .onFirst()
            .performTouchInput { longClick() }

        composeTestRule
            .onNodeWithTag(SELECTED_ICON)
            .isDisplayed()
    }

    @Test
    fun whenGrid_onLongClick_showSelectedIcon() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalSnackbar provides SnackbarHostState()) {
                FavoriteScreen(
                    viewState = ViewState.FavoritesLoaded(validItems, ViewType.GRID_2_COLUMN),
                    viewEffect = null,
                    inSelection = false,
                    onAction = {},
                    onAudioClick = {}
                )
            }
        }

        composeTestRule
            .onAllNodesWithTag(GRID_ITEM)
            .onFirst()
            .performTouchInput { longClick() }

        composeTestRule
            .onNodeWithTag(SELECTED_ICON)
            .isDisplayed()
    }

}

private fun SemanticsNodeInteractionCollection.assertAllIsDisplayed(): SemanticsNodeInteractionCollection {
    fetchSemanticsNodes().forEachIndexed { index, _ ->
        get(index).assertIsDisplayed()
    }
    return this
}

private fun SemanticsNodeInteractionCollection.assertAllIsNotDisplayed(): SemanticsNodeInteractionCollection {
    fetchSemanticsNodes().forEachIndexed { index, _ ->
        get(index).assertIsNotDisplayed()
    }
    return this
}