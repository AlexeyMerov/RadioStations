package com.alexeymerov.radiostations.feature.profile

import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.core.domain.usecase.profile.ProfileUsaCase
import com.alexeymerov.radiostations.core.dto.Country
import com.alexeymerov.radiostations.core.dto.TextFieldData
import com.alexeymerov.radiostations.core.dto.UserDto
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.common.BaseViewAction
import com.alexeymerov.radiostations.core.ui.common.BaseViewEffect
import com.alexeymerov.radiostations.core.ui.common.BaseViewModel
import com.alexeymerov.radiostations.core.ui.common.BaseViewState
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel.ViewAction
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel.ViewEffect
import com.alexeymerov.radiostations.feature.profile.ProfileViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUsaCase: ProfileUsaCase
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    val tempUri = profileUsaCase.getAvatarTempUri()

    var countryCodes = emptyList<Country>()
        private set

    private val tempUserData = MutableStateFlow<UserDto?>(null)

    val userData = profileUsaCase.getUserData()
        .combine(tempUserData) { original, temp ->
            if (viewState.value is ViewState.InEdit && temp != null) {
                return@combine temp.copy(
                    avatarFile = original.avatarFile,
                    name = temp.name,
                    email = temp.email,
                    country = temp.country,
                    phoneNumber = temp.phoneNumber
                )
            }

            return@combine original
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun createInitialState(): ViewState = ViewState.Loaded

    override fun handleAction(action: ViewAction) {
        viewModelScope.launch(ioContext) {
            Timber.d("handleAction: $action")
            when (action) {
                is ViewAction.EnterEditMode -> onEnterEditMode()
                is ViewAction.SaveEditsAndExitMode -> handleSaveEdits()

                is ViewAction.SaveCameraImage -> saveImageFromCamera()
                is ViewAction.SaveGalleryImage -> saveImageFromGallery(action.uri)
                is ViewAction.DeleteImage -> deleteAvatar()

                is ViewAction.NewName -> handleNewName(action.name)
                is ViewAction.NewEmail -> handleNewEmail(action.email)
                is ViewAction.NewCountry -> handleNewCountry(action)
                is ViewAction.NewPhone -> handleNewPhoneValue(action.phone)
            }
        }
    }

    private suspend fun saveImageFromCamera() {
        profileUsaCase.saveAvatar(tempUri, true)
    }

    private suspend fun saveImageFromGallery(uri: Uri) {
        profileUsaCase.saveAvatar(uri, false)
    }

    private suspend fun deleteAvatar() {
        profileUsaCase.deleteAvatar()
    }

    private fun onEnterEditMode() {
        val result = mutableListOf<Country>()
        repeat(9) { result.add(Country(it + 1)) }
        countryCodes = result

        tempUserData.value = userData.value
        setState(ViewState.InEdit)
    }

    private fun handleNewName(name: String) {
        val errorIfExist = when {
            name.isEmpty() -> R.string.cannot_be_empty
            name.length < 2 -> R.string.too_short
            else -> null
        }
        val result = TextFieldData(
            text = name,
            errorTextResId = errorIfExist
        )
        tempUserData.value = tempUserData.value?.copy(
            name = result,
            isEverythingValid = errorIfExist == null
        )
    }

    private fun handleNewEmail(email: String) {
        val errorIfExist = when {
            email.isEmpty() -> R.string.cannot_be_empty
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.incorrect
            else -> null
        }

        val result = TextFieldData(
            text = email,
            errorTextResId = errorIfExist
        )
        tempUserData.value = tempUserData.value?.copy(
            email = result,
            isEverythingValid = errorIfExist == null
        )
    }

    private fun handleNewCountry(action: ViewAction.NewCountry) {
        tempUserData.value = tempUserData.value?.copy(country = action.country)
    }

    private fun handleNewPhoneValue(phone: String) {
        val errorIfExist = when {
            phone.isEmpty() -> R.string.cannot_be_empty
            phone.length < 5 -> R.string.too_short
            else -> null
        }
        val result = TextFieldData(
            text = phone,
            errorTextResId = errorIfExist
        )
        tempUserData.value = tempUserData.value?.copy(
            phoneNumber = result,
            isEverythingValid = errorIfExist == null
        )
    }

    private suspend fun handleSaveEdits() {
        tempUserData.value?.let {
            profileUsaCase.saveUserData(it)
            tempUserData.value = null
            setState(ViewState.Loaded)
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loaded : ViewState
        data object InEdit : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class SaveGalleryImage(val uri: Uri) : ViewAction
        data object SaveCameraImage : ViewAction
        data object DeleteImage : ViewAction

        data object EnterEditMode : ViewAction
        data object SaveEditsAndExitMode : ViewAction

        data class NewName(val name: String) : ViewAction
        data class NewEmail(val email: String) : ViewAction
        data class NewCountry(val country: Country) : ViewAction
        data class NewPhone(val phone: String) : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect
    }
}