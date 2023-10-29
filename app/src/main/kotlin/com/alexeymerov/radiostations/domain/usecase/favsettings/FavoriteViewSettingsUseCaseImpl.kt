package com.alexeymerov.radiostations.domain.usecase.favsettings

import androidx.datastore.preferences.core.intPreferencesKey
import com.alexeymerov.radiostations.data.local.datastore.SettingsStore
import com.alexeymerov.radiostations.domain.usecase.favsettings.FavoriteViewSettingsUseCase.ViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteViewSettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore
) : FavoriteViewSettingsUseCase {

    override fun getViewType(): Flow<ViewType> {
        return settingsStore.getIntPrefsFlow(VIEW_TYPE_KEY)
            .map { ViewType.values()[it ?: 0] }
    }

    override suspend fun setNextViewType(currentValue: ViewType) {
        val lastIndex = ViewType.values().size - 1
        var newValue = currentValue.ordinal
        if (newValue == lastIndex) {
            newValue = 0
        } else {
            newValue++
        }

        settingsStore.satPrefs(VIEW_TYPE_KEY, newValue)
    }

    companion object {
        val VIEW_TYPE_KEY = intPreferencesKey("view_type")
    }
}