package com.alexeymerov.radiostations.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.alexeymerov.radiostations.core.common.ThemeState
import com.alexeymerov.radiostations.core.domain.usecase.settings.connectivity.ConnectivitySettingsUseCase
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags.PAGER
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags.TAB_ITEM
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags.TAB_ROW
import org.junit.Rule
import org.junit.Test


class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenStateIsLoading_loaderIsShown() {
        composeTestRule.setContent {
            SettingsScreen(SettingsViewModel.ViewState.Loading) {}
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.LOADER)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_contentIsShown() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(SettingsTestTags.CONTENT)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(TAB_ROW)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_firstTabIsShown() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(SettingsTestTags.LANGUAGE_BUTTON)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.THEME_BUTTON)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_secondTabIsShown() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(TAB_ITEM + SettingTab.DEV.index)
            .performClick()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.CONNECTIVITY_VIEW)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_swipeChangeTabAndContent() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        val pager = composeTestRule.onNodeWithTag(PAGER)
        val firstTab = composeTestRule.onNodeWithTag(TAB_ITEM + SettingTab.USER.index)
        val secondTab = composeTestRule.onNodeWithTag(TAB_ITEM + SettingTab.DEV.index)
        val themeButton = composeTestRule.onNodeWithTag(SettingsTestTags.THEME_BUTTON)

        firstTab.assertIsSelected()
        themeButton.assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.CONNECTIVITY_VIEW)
            .assertIsNotDisplayed()

        pager.performTouchInput { swipeLeft() }

        val connectionButton = composeTestRule.onNodeWithTag(SettingsTestTags.CONNECTIVITY_VIEW)

        secondTab.assertIsSelected()
        themeButton.assertIsNotDisplayed()
        connectionButton.assertIsDisplayed()

        pager.performTouchInput { swipeRight() }

        firstTab.assertIsSelected()
        themeButton.assertIsDisplayed()
        connectionButton.assertIsNotDisplayed()
    }

    @Test
    fun connectionSwitch_whenOnline_isOn() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(TAB_ITEM + SettingTab.DEV.index)
            .performClick()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.CONNECTIVITY_SWITCH)
            .assertIsOn()
    }

    @Test
    fun connectionSwitch_whenOffline_isOff() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.OFFLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(TAB_ITEM + SettingTab.DEV.index)
            .performClick()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.CONNECTIVITY_SWITCH)
            .assertIsOff()
    }

    @Test
    fun onThemeClick_showsDialog() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(SettingsTestTags.THEME_BUTTON)
            .performClick()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.THEME_DIALOG)
            .assertIsDisplayed()
    }

    @Test
    fun onLanguageClick_showsBottomSheet() {
        composeTestRule.setContent {
            SettingsScreen(
                SettingsViewModel.ViewState.Loaded(
                    themeState = ThemeState(),
                    connectionStatus = ConnectivitySettingsUseCase.ConnectionStatus.ONLINE
                )
            ) {}
        }

        composeTestRule
            .onNodeWithTag(SettingsTestTags.LANGUAGE_BUTTON)
            .performClick()

        composeTestRule
            .onNodeWithTag(SettingsTestTags.LANGUAGE_SHEET)
            .assertIsDisplayed()
    }

}