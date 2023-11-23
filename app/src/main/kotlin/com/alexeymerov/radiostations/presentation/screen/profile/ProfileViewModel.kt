package com.alexeymerov.radiostations.presentation.screen.profile

import com.alexeymerov.radiostations.common.BaseViewAction
import com.alexeymerov.radiostations.common.BaseViewEffect
import com.alexeymerov.radiostations.common.BaseViewModel
import com.alexeymerov.radiostations.common.BaseViewState
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.screen.profile.ProfileViewModel.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel<ViewState, ViewAction, ViewEffect>() {


    override fun createInitialState(): ViewState = ViewState.Loading

    override fun handleAction(action: ViewAction) {

    }

    sealed interface ViewState : BaseViewState {
        data object Loading : ViewState
        data object Loaded : ViewState
    }

    sealed interface ViewAction : BaseViewAction

    sealed interface ViewEffect : BaseViewEffect {
        class ShowToast(val text: String) : ViewEffect //errors or anything
    }

}