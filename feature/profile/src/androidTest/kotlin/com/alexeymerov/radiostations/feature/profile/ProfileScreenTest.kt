package com.alexeymerov.radiostations.feature.profile

import android.R
import android.net.Uri
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasPerformImeAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.view.CommonViewTestTags
import com.alexeymerov.radiostations.feature.profile.ProfileTestTags.INPUT_FIELD_ERROR
import com.alexeymerov.radiostations.feature.profile.ProfileTestTags.TEXT_FIELD
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test


class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val validUserData = UserDto(
        avatarFile = null,
        name = TextFieldData("Name"),
        email = TextFieldData("Email"),
        phoneNumber = TextFieldData("2345"),
        countryCode = 1,
        isEverythingValid = true
    )

    private val validLoadedState = ProfileViewModel.ViewState.Loaded(validUserData)
    private val validInEditState = ProfileViewModel.ViewState.InEdit(validUserData, Uri.EMPTY, emptyFlow())

    @Test
    fun whenStateIsLoading_loaderIsShown() {
        composeTestRule.setContent {
            ProfileScreen(ProfileViewModel.ViewState.Loading, {}) {}
        }

        composeTestRule
            .onNodeWithTag(CommonViewTestTags.LOADER)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsLoaded_mainContentIsShown() {
        composeTestRule.setContent {
            ProfileScreen(validLoadedState, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ProfileTestTags.MAIN_CONTENT)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE_EDIT)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE_REMOVE)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.EDIT_SAVE_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_NAME_FIELD)
            .onChildren()
            .filterToOne(hasTestTag(TEXT_FIELD))
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_EMAIL_FIELD)
            .onChildren()
            .filterToOne(hasTestTag(TEXT_FIELD))
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_PHONE_FIELD)
            .onChildren()
            .filterToOne(hasTestTag(TEXT_FIELD))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun whenStateIsInEdit_mainContentWithEditStatesIsShown() {
        composeTestRule.setContent {
            ProfileScreen(validInEditState, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ProfileTestTags.MAIN_CONTENT)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE_EDIT)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE_REMOVE)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.EDIT_SAVE_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_NAME_FIELD)
            .assertIsDisplayed()
            .assertIsEnabled()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_EMAIL_FIELD)
            .assertIsDisplayed()
            .assertIsEnabled()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_PHONE_FIELD)
            .assertIsDisplayed()
            .assertIsEnabled()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.COUNTRY_CODE_BOX)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun onEditImageClick_showsImageSelectSheet() {
        composeTestRule.setContent {
            ProfileScreen(validInEditState, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE_EDIT)
            .performClick()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.IMAGE_CHOOSE_SHEET)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateInEdit_onCountryBoxClick_showsCountrySheet() {
        composeTestRule.setContent {
            ProfileScreen(validInEditState, {}) {}
        }

        composeTestRule
            .onNodeWithTag(ProfileTestTags.COUNTRY_CODE_BOX)
            .performClick()

        composeTestRule
            .onNodeWithTag(ProfileTestTags.COUNTRY_CODE_SHEET)
            .assertIsDisplayed()
    }

    @Test
    fun whenStateInEdit_canEnterTextInNameField() {
        composeTestRule.setContent {
            ProfileScreen(validInEditState, {}) {}
        }

        val textField = composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_NAME_FIELD)
            .onChildren()
            .filterToOne(hasPerformImeAction())

        val text = "Some name"

        textField.performTextClearance()
        textField.performTextInput(text)
    }

    @Test
    fun whenStateInEdit_canEnterTextInEmailField() {
        composeTestRule.setContent {
            ProfileScreen(validInEditState, {}) {}
        }

        val textField = composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_EMAIL_FIELD)
            .onChildren()
            .filterToOne(hasPerformImeAction())

        val text = "email@email.com"

        textField.performTextClearance()
        textField.performTextInput(text)
    }

    @Test
    fun whenStateInEdit_canEnterTextInPhoneField() {
        composeTestRule.setContent {
            ProfileScreen(validInEditState, {}) {}
        }

        val textField = composeTestRule
            .onNodeWithTag(ProfileTestTags.USER_PHONE_FIELD)
            .onChildren()
            .filterToOne(hasPerformImeAction())

        val text = "1234"

        textField.performTextClearance()
        textField.performTextInput(text)
    }

    @Test
    fun whenStateInEdit_andDataInvalid_showErrors() {
        val viewStateWithInvalidData = validUserData.copy(
            name = TextFieldData("Name", R.string.unknownName),
            email = TextFieldData("Email", R.string.unknownName),
            phoneNumber = TextFieldData("2345", R.string.unknownName),
            isEverythingValid = false
        )
        composeTestRule.setContent {
            ProfileScreen(validInEditState.copy(data = viewStateWithInvalidData), {}) {}
        }

        composeTestRule
            .onNodeWithTag(ProfileTestTags.EDIT_SAVE_ICON)
            .assertIsNotDisplayed()

        composeTestRule
            .onAllNodesWithTag(INPUT_FIELD_ERROR)
            .assertAllIsDisplayed()
    }

    private fun SemanticsNodeInteractionCollection.assertAllIsDisplayed(): SemanticsNodeInteractionCollection {
        fetchSemanticsNodes().forEachIndexed { index, _ ->
            get(index).assertIsDisplayed()
        }
        return this
    }

}