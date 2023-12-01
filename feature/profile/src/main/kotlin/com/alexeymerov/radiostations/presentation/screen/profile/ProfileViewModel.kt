package com.alexeymerov.radiostations.presentation.screen.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.domain.usecase.profile.ProfileUsaCase
import com.alexeymerov.radiostations.presentation.common.BaseViewAction
import com.alexeymerov.radiostations.presentation.common.BaseViewEffect
import com.alexeymerov.radiostations.presentation.common.BaseViewModel
import com.alexeymerov.radiostations.presentation.common.BaseViewState
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUsaCase: ProfileUsaCase
) : BaseViewModel<ViewState, ViewAction, ViewEffect>() {

    var avatar = mutableStateOf<File?>(null)

    val tempUri = profileUsaCase.getTempUri()

    init {
        updateAvatar()
    }

    private fun updateAvatar() {
        viewModelScope.launch(ioContext) {
            val newFile = profileUsaCase.getAvatar()
            withContext(Dispatchers.Main) {
                avatar.value = newFile
            }
        }
    }

    override fun createInitialState(): ViewState = ViewState.Loaded

    override fun handleAction(action: ViewAction) {
        viewModelScope.launch(ioContext) {
            Timber.d("handleAction: $action")
            when (action) {
                ViewAction.SaveCameraImage -> {
                    profileUsaCase.saveAvatar(tempUri, true)
                    updateAvatar()
                }

                is ViewAction.SaveGalleryImage -> {
                    profileUsaCase.saveAvatar(action.uri, false)
                    updateAvatar()
                }
            }
        }
    }

    sealed interface ViewState : BaseViewState {
        data object Loaded : ViewState
    }

    sealed interface ViewAction : BaseViewAction {
        data class SaveGalleryImage(val uri: Uri) : ViewAction
        data object SaveCameraImage : ViewAction
    }

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}