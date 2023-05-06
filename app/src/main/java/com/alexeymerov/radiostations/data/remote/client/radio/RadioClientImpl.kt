package com.alexeymerov.radiostations.data.remote.client.radio

import com.alexeymerov.radiostations.data.remote.api.RadioApi
import com.alexeymerov.radiostations.data.remote.client.BaseClient
import timber.log.Timber
import javax.inject.Inject

class RadioClientImpl @Inject constructor(private val radioApi: RadioApi) : RadioClient, BaseClient<RadioApi>(radioApi) {

    override suspend fun loadCategories() {
        val categories = radioApi.getCategories()
        if (categories.head.status == "200") {
            Timber.d("Merov List size: ${categories.body}")
        }
    }
}