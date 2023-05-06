package com.alexeymerov.radiostations.data.remote.client.radio

interface RadioClient {

    suspend fun loadCategories()

}