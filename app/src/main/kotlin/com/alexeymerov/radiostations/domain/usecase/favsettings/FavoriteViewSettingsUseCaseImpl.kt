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
        return settingsStore.getIntPrefsFlow(VIEW_TYPE_KEY, defValue = 0)
            .map { ViewType.values()[it] }
    }

    override suspend fun setViewType(type: ViewType) {
        settingsStore.satPrefs(VIEW_TYPE_KEY, type.ordinal)
    }

    companion object {
        val VIEW_TYPE_KEY = intPreferencesKey("view_type")
    }
}