package com.example.radiostations.data.remote.client.radio

import com.example.radiostations.data.remote.api.RadioApi
import com.example.radiostations.data.remote.client.BaseClient
import timber.log.Timber
import javax.inject.Inject

class RadioClientImpl @Inject constructor(private val radioApi: RadioApi) : RadioClient, BaseClient<RadioApi>(radioApi) {

    override suspend fun loadCategories() {
        val categories = radioApi.getCategories()
        Timber.d("Merov List size: ${categories.body.size}")
    }
}