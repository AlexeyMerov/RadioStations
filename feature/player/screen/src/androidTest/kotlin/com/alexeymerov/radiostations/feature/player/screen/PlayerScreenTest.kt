package com.alexeymerov.radiostations.feature.player.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.alexeymerov.radiostations.core.dto.AudioItemDto
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags
import org.junit.Rule
import org.junit.Test

class PlayerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val validItem = AudioItemDto(
        parentUrl = "parenturl",
        directUrl = "directUrl",
        image = "image",
        title = "title",
        subTitle = "subTitle"
    )

    @Test
    fun whenStateIsLoading_loaderIsShown() {
        composeTestRule.setContent {
            PlayerScreen(PlayerViewModel.ViewState.Loading) {}
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.LOADER)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsError_errorIsShown() {
        composeTestRule.setContent {
            PlayerScreen(PlayerViewModel.ViewState.Error) {}
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.ERROR_TAG)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateReadyToPlay_contentIsShown() {
        composeTestRule.setContent {
            PlayerScreen(PlayerViewModel.ViewState.ReadyToPlay(validItem, PlayerViewModel.ScreenPlayState.STOPPED)) {}
        }

        composeTestRule
            .onNodeWithTag(PlayerScreenTestTags.ARTWORK)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(PlayerScreenTestTags.PLAY_BUTTON)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateReadyToPlay_andPlayStateLoading_loaderButtonIsShown() {
        composeTestRule.setContent {
            PlayerScreen(PlayerViewModel.ViewState.ReadyToPlay(validItem, PlayerViewModel.ScreenPlayState.LOADING)) {}
        }

        composeTestRule
            .onNodeWithTag(PlayerScreenTestTags.PLAY_BUTTON_LOADER)
            .assertIsDisplayed()
    }

}