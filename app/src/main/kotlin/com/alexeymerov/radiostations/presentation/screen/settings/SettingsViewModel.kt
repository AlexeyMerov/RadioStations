package com.alexeymerov.radiostations.presentation.screen.settings

import androidx.lifecycle.ViewModel
import com.alexeymerov.radiostations.domain.usecase.usersettings.UserSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsUseCase: UserSettingsUseCase) : ViewModel() {

}