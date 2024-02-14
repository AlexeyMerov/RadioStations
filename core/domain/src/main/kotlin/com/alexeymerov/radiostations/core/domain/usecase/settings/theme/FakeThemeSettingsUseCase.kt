package com.alexeymerov.radiostations.core.domain.usecase.settings.theme

import com.alexeymerov.radiostations.core.domain.usecase.settings.theme.ThemeSettingsUseCase.ThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class FakeThemeSettingsUseCase : ThemeSettingsUseCase {

    private var currentDelay = 0L

    private val currentState = MutableStateFlow(ThemeState())

    override fun getThemeState(): Flow<ThemeState> {
        return currentState.onStart { delay(currentDelay) }
    }

    override suspend fun updateThemeState(state: ThemeState) {
        currentState.value = state
    }

    fun addDelayToFlow(delay: Long) {
        currentDelay = delay
    }
}