package com.alexeymerov.radiostations.domain.usecase.settings.favorite

import com.alexeymerov.radiostations.data.local.datastore.SettingsStore
import com.alexeymerov.radiostations.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.ViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteViewSettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore
) : FavoriteViewSettingsUseCase {

    override fun getViewType(): Flow<ViewType> {
        return settingsStore.getIntPrefsFlow(VIEW_TYPE_KEY, defValue = 0)
            .map { prefValue ->
                ViewType.values().first { it.value == prefValue }
            }
    }

    override suspend fun setViewType(type: ViewType) {
        settingsStore.setIntPrefs(VIEW_TYPE_KEY, type.value)
    }

    companion object {
        const val VIEW_TYPE_KEY = "view_type"
    }
}