package com.example.radiostations.data.remote.client.radio

interface RadioClient {

    suspend fun loadCategories()

}